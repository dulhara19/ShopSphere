'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  CreditCard,
  MapPin,
  Truck,
  Check,
  Loader2,
  ChevronRight,
  Lock,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { useCartStore } from '@/stores/cart-store';
import { useAuthStore } from '@/stores/auth-store';
import { userApi } from '@/lib/api/user';
import { shippingApi } from '@/lib/api/shipping';
import { orderApi } from '@/lib/api/order';
import { formatPriceSimple } from '@/lib/utils/format';
import { useToast } from '@/hooks/use-toast';

const shippingSchema = z.object({
  name: z.string().min(1, 'Name is required'),
  addressLine1: z.string().min(1, 'Address is required'),
  addressLine2: z.string().optional(),
  city: z.string().min(1, 'City is required'),
  state: z.string().min(1, 'State is required'),
  postalCode: z.string().min(1, 'Postal code is required'),
  country: z.string().min(1, 'Country is required'),
  phone: z.string().optional(),
});

const paymentSchema = z.object({
  cardNumber: z.string().min(16, 'Invalid card number'),
  expiryDate: z.string().regex(/^\d{2}\/\d{2}$/, 'Invalid expiry date (MM/YY)'),
  cvv: z.string().min(3, 'Invalid CVV'),
  cardholderName: z.string().min(1, 'Cardholder name is required'),
});

type ShippingFormValues = z.infer<typeof shippingSchema>;
type PaymentFormValues = z.infer<typeof paymentSchema>;

type CheckoutStep = 'shipping' | 'payment' | 'review';

export default function CheckoutPage() {
  const router = useRouter();
  const { toast } = useToast();
  const { cart, fetchCart, clearCart } = useCartStore();
  const { isAuthenticated, user } = useAuthStore();
  const [currentStep, setCurrentStep] = useState<CheckoutStep>('shipping');
  const [shippingData, setShippingData] = useState<ShippingFormValues | null>(null);
  const [selectedShippingRate, setSelectedShippingRate] = useState<string>('');
  const [isProcessing, setIsProcessing] = useState(false);

  const { data: addressesData } = useQuery({
    queryKey: ['addresses'],
    queryFn: () => userApi.getAddresses(),
    enabled: isAuthenticated,
  });

  const { data: shippingRatesData, isLoading: ratesLoading } = useQuery({
    queryKey: ['shippingRates', shippingData],
    queryFn: () =>
      shippingApi.getShippingRates({
        destinationAddress: shippingData!,
        items: cart?.items.map((i) => ({
          productId: i.productId,
          quantity: i.quantity,
        })) || [],
      }),
    enabled: !!shippingData && !!cart,
  });

  const addresses = addressesData?.data.data || [];
  const shippingRates = shippingRatesData?.data.data || [];
  const selectedRate = shippingRates.find((r) => r.id === selectedShippingRate);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login?redirect=/checkout');
    }
  }, [isAuthenticated, router]);

  const shippingForm = useForm<ShippingFormValues>({
    resolver: zodResolver(shippingSchema),
    defaultValues: {
      name: user?.firstName && user?.lastName ? `${user.firstName} ${user.lastName}` : '',
      addressLine1: '',
      addressLine2: '',
      city: '',
      state: '',
      postalCode: '',
      country: 'US',
      phone: user?.phone || '',
    },
  });

  const paymentForm = useForm<PaymentFormValues>({
    resolver: zodResolver(paymentSchema),
    defaultValues: {
      cardNumber: '',
      expiryDate: '',
      cvv: '',
      cardholderName: '',
    },
  });

  const handleSelectAddress = (addressId: string) => {
    const address = addresses.find((a) => a.id === addressId);
    if (address) {
      shippingForm.reset({
        name: address.name,
        addressLine1: address.addressLine1,
        addressLine2: address.addressLine2 || '',
        city: address.city,
        state: address.state,
        postalCode: address.postalCode,
        country: address.country,
        phone: address.phone || '',
      });
    }
  };

  const onShippingSubmit = (data: ShippingFormValues) => {
    setShippingData(data);
    setCurrentStep('payment');
  };

  const onPaymentSubmit = () => {
    setCurrentStep('review');
  };

  const handlePlaceOrder = async () => {
    if (!cart || !shippingData || !selectedRate) return;

    setIsProcessing(true);
    try {
      const response = await orderApi.checkout({
        shippingAddressId: undefined, // Use form data
        shippingAddress: shippingData,
        shippingMethodId: selectedRate.id,
        paymentMethodId: 'card',
      });

      await clearCart();
      toast({
        title: 'Order placed successfully!',
        description: `Order #${response.data.data.orderNumber} has been created.`,
      });
      router.push(`/orders/${response.data.data.id}`);
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to place order. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsProcessing(false);
    }
  };

  if (!cart || cart.items.length === 0) {
    return (
      <div className="container py-16 text-center">
        <h1 className="text-2xl font-bold mb-4">Your cart is empty</h1>
        <Button onClick={() => router.push('/products')}>Go Shopping</Button>
      </div>
    );
  }

  const steps = [
    { id: 'shipping', label: 'Shipping', icon: MapPin },
    { id: 'payment', label: 'Payment', icon: CreditCard },
    { id: 'review', label: 'Review', icon: Check },
  ];

  return (
    <div className="container py-8">
      <h1 className="text-2xl font-bold mb-8">Checkout</h1>

      {/* Progress Steps */}
      <div className="flex items-center justify-center mb-8">
        {steps.map((step, index) => (
          <div key={step.id} className="flex items-center">
            <div
              className={`flex items-center justify-center w-10 h-10 rounded-full border-2 ${
                currentStep === step.id
                  ? 'border-primary bg-primary text-primary-foreground'
                  : steps.findIndex((s) => s.id === currentStep) > index
                  ? 'border-primary bg-primary text-primary-foreground'
                  : 'border-muted text-muted-foreground'
              }`}
            >
              {steps.findIndex((s) => s.id === currentStep) > index ? (
                <Check className="h-5 w-5" />
              ) : (
                <step.icon className="h-5 w-5" />
              )}
            </div>
            <span
              className={`ml-2 text-sm font-medium ${
                currentStep === step.id ? 'text-primary' : 'text-muted-foreground'
              }`}
            >
              {step.label}
            </span>
            {index < steps.length - 1 && (
              <ChevronRight className="h-5 w-5 mx-4 text-muted-foreground" />
            )}
          </div>
        ))}
      </div>

      <div className="grid lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="lg:col-span-2">
          {currentStep === 'shipping' && (
            <Card>
              <CardHeader>
                <CardTitle>Shipping Address</CardTitle>
                <CardDescription>
                  Enter your shipping details
                </CardDescription>
              </CardHeader>
              <CardContent>
                {addresses.length > 0 && (
                  <div className="mb-6">
                    <label className="text-sm font-medium mb-2 block">
                      Use saved address
                    </label>
                    <Select onValueChange={handleSelectAddress}>
                      <SelectTrigger>
                        <SelectValue placeholder="Select an address" />
                      </SelectTrigger>
                      <SelectContent>
                        {addresses.map((address) => (
                          <SelectItem key={address.id} value={address.id}>
                            {address.name} - {address.addressLine1}, {address.city}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <Separator className="my-4" />
                  </div>
                )}

                <Form {...shippingForm}>
                  <form
                    onSubmit={shippingForm.handleSubmit(onShippingSubmit)}
                    className="space-y-4"
                  >
                    <FormField
                      control={shippingForm.control}
                      name="name"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Full Name</FormLabel>
                          <FormControl>
                            <Input {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={shippingForm.control}
                      name="addressLine1"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Address Line 1</FormLabel>
                          <FormControl>
                            <Input {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={shippingForm.control}
                      name="addressLine2"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Address Line 2 (Optional)</FormLabel>
                          <FormControl>
                            <Input {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={shippingForm.control}
                        name="city"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>City</FormLabel>
                            <FormControl>
                              <Input {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={shippingForm.control}
                        name="state"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>State</FormLabel>
                            <FormControl>
                              <Input {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <FormField
                        control={shippingForm.control}
                        name="postalCode"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Postal Code</FormLabel>
                            <FormControl>
                              <Input {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={shippingForm.control}
                        name="country"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Country</FormLabel>
                            <Select
                              onValueChange={field.onChange}
                              defaultValue={field.value}
                            >
                              <FormControl>
                                <SelectTrigger>
                                  <SelectValue />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                <SelectItem value="US">United States</SelectItem>
                                <SelectItem value="CA">Canada</SelectItem>
                                <SelectItem value="UK">United Kingdom</SelectItem>
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <FormField
                      control={shippingForm.control}
                      name="phone"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Phone (Optional)</FormLabel>
                          <FormControl>
                            <Input type="tel" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <Button type="submit" className="w-full">
                      Continue to Payment
                      <ChevronRight className="ml-2 h-4 w-4" />
                    </Button>
                  </form>
                </Form>
              </CardContent>
            </Card>
          )}

          {currentStep === 'payment' && (
            <div className="space-y-6">
              {/* Shipping Method Selection */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Truck className="h-5 w-5" />
                    Shipping Method
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  {ratesLoading ? (
                    <div className="space-y-3">
                      {[1, 2, 3].map((i) => (
                        <Skeleton key={i} className="h-16 w-full" />
                      ))}
                    </div>
                  ) : (
                    <RadioGroup
                      value={selectedShippingRate}
                      onValueChange={setSelectedShippingRate}
                    >
                      {shippingRates.map((rate) => (
                        <div
                          key={rate.id}
                          className={`flex items-center justify-between p-4 border rounded-lg cursor-pointer ${
                            selectedShippingRate === rate.id
                              ? 'border-primary bg-primary/5'
                              : ''
                          }`}
                          onClick={() => setSelectedShippingRate(rate.id)}
                        >
                          <div className="flex items-center gap-3">
                            <RadioGroupItem value={rate.id} />
                            <div>
                              <p className="font-medium">{rate.name}</p>
                              <p className="text-sm text-muted-foreground">
                                {rate.estimatedDays} business days
                              </p>
                            </div>
                          </div>
                          <span className="font-medium">
                            {rate.price === 0
                              ? 'Free'
                              : formatPriceSimple(rate.price)}
                          </span>
                        </div>
                      ))}
                    </RadioGroup>
                  )}
                </CardContent>
              </Card>

              {/* Payment Details */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <CreditCard className="h-5 w-5" />
                    Payment Details
                  </CardTitle>
                  <CardDescription>
                    Your payment information is encrypted and secure
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <Form {...paymentForm}>
                    <form
                      onSubmit={paymentForm.handleSubmit(onPaymentSubmit)}
                      className="space-y-4"
                    >
                      <FormField
                        control={paymentForm.control}
                        name="cardholderName"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Cardholder Name</FormLabel>
                            <FormControl>
                              <Input placeholder="John Doe" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={paymentForm.control}
                        name="cardNumber"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Card Number</FormLabel>
                            <FormControl>
                              <Input
                                placeholder="4242 4242 4242 4242"
                                {...field}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <div className="grid grid-cols-2 gap-4">
                        <FormField
                          control={paymentForm.control}
                          name="expiryDate"
                          render={({ field }) => (
                            <FormItem>
                              <FormLabel>Expiry Date</FormLabel>
                              <FormControl>
                                <Input placeholder="MM/YY" {...field} />
                              </FormControl>
                              <FormMessage />
                            </FormItem>
                          )}
                        />

                        <FormField
                          control={paymentForm.control}
                          name="cvv"
                          render={({ field }) => (
                            <FormItem>
                              <FormLabel>CVV</FormLabel>
                              <FormControl>
                                <Input
                                  type="password"
                                  placeholder="123"
                                  maxLength={4}
                                  {...field}
                                />
                              </FormControl>
                              <FormMessage />
                            </FormItem>
                          )}
                        />
                      </div>

                      <div className="flex gap-4 pt-4">
                        <Button
                          type="button"
                          variant="outline"
                          onClick={() => setCurrentStep('shipping')}
                        >
                          Back
                        </Button>
                        <Button
                          type="submit"
                          className="flex-1"
                          disabled={!selectedShippingRate}
                        >
                          Review Order
                          <ChevronRight className="ml-2 h-4 w-4" />
                        </Button>
                      </div>
                    </form>
                  </Form>
                </CardContent>
              </Card>
            </div>
          )}

          {currentStep === 'review' && (
            <Card>
              <CardHeader>
                <CardTitle>Review Your Order</CardTitle>
                <CardDescription>
                  Please review your order details before placing
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Shipping Address */}
                <div>
                  <h3 className="font-medium mb-2 flex items-center gap-2">
                    <MapPin className="h-4 w-4" />
                    Shipping Address
                  </h3>
                  <p className="text-sm text-muted-foreground">
                    {shippingData?.name}
                    <br />
                    {shippingData?.addressLine1}
                    {shippingData?.addressLine2 && (
                      <>
                        <br />
                        {shippingData.addressLine2}
                      </>
                    )}
                    <br />
                    {shippingData?.city}, {shippingData?.state}{' '}
                    {shippingData?.postalCode}
                    <br />
                    {shippingData?.country}
                  </p>
                  <Button
                    variant="link"
                    className="p-0 h-auto"
                    onClick={() => setCurrentStep('shipping')}
                  >
                    Edit
                  </Button>
                </div>

                <Separator />

                {/* Shipping Method */}
                <div>
                  <h3 className="font-medium mb-2 flex items-center gap-2">
                    <Truck className="h-4 w-4" />
                    Shipping Method
                  </h3>
                  <p className="text-sm text-muted-foreground">
                    {selectedRate?.name} - {selectedRate?.estimatedDays} business
                    days
                  </p>
                  <Button
                    variant="link"
                    className="p-0 h-auto"
                    onClick={() => setCurrentStep('payment')}
                  >
                    Edit
                  </Button>
                </div>

                <Separator />

                {/* Payment */}
                <div>
                  <h3 className="font-medium mb-2 flex items-center gap-2">
                    <CreditCard className="h-4 w-4" />
                    Payment Method
                  </h3>
                  <p className="text-sm text-muted-foreground">
                    Card ending in ****
                    {paymentForm.getValues('cardNumber').slice(-4)}
                  </p>
                  <Button
                    variant="link"
                    className="p-0 h-auto"
                    onClick={() => setCurrentStep('payment')}
                  >
                    Edit
                  </Button>
                </div>

                <Separator />

                {/* Order Items */}
                <div>
                  <h3 className="font-medium mb-4">Order Items</h3>
                  <div className="space-y-3">
                    {cart.items.map((item) => (
                      <div key={item.id} className="flex gap-3">
                        <div className="relative h-16 w-16 rounded bg-muted overflow-hidden">
                          <Image
                            src={
                              item.productImage || '/images/placeholder-product.svg'
                            }
                            alt={item.productName}
                            fill
                            className="object-cover"
                          />
                        </div>
                        <div className="flex-1">
                          <p className="font-medium text-sm">
                            {item.productName}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            Qty: {item.quantity}
                          </p>
                        </div>
                        <p className="font-medium text-sm">
                          {formatPriceSimple(item.totalPrice)}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>
              </CardContent>
              <CardFooter className="flex-col gap-4">
                <Button
                  className="w-full"
                  size="lg"
                  onClick={handlePlaceOrder}
                  disabled={isProcessing}
                >
                  {isProcessing ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Processing...
                    </>
                  ) : (
                    <>
                      <Lock className="mr-2 h-4 w-4" />
                      Place Order
                    </>
                  )}
                </Button>
                <p className="text-xs text-center text-muted-foreground">
                  By placing your order, you agree to our Terms of Service and
                  Privacy Policy
                </p>
              </CardFooter>
            </Card>
          )}
        </div>

        {/* Order Summary Sidebar */}
        <div>
          <Card className="sticky top-24">
            <CardHeader>
              <CardTitle>Order Summary</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-3 max-h-64 overflow-y-auto">
                {cart.items.map((item) => (
                  <div key={item.id} className="flex gap-3">
                    <div className="relative h-12 w-12 rounded bg-muted overflow-hidden">
                      <Image
                        src={item.productImage || '/images/placeholder-product.svg'}
                        alt={item.productName}
                        fill
                        className="object-cover"
                      />
                      <span className="absolute -top-1 -right-1 bg-primary text-primary-foreground text-xs rounded-full h-5 w-5 flex items-center justify-center">
                        {item.quantity}
                      </span>
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium truncate">
                        {item.productName}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {formatPriceSimple(item.unitPrice)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>

              <Separator />

              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Subtotal</span>
                  <span>{formatPriceSimple(cart.subtotal)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Shipping</span>
                  <span>
                    {selectedRate
                      ? selectedRate.price === 0
                        ? 'Free'
                        : formatPriceSimple(selectedRate.price)
                      : 'Calculated next'}
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Estimated Tax</span>
                  <span>Calculated at checkout</span>
                </div>
                <Separator />
                <div className="flex justify-between font-medium">
                  <span>Total</span>
                  <span>
                    {formatPriceSimple(
                      cart.subtotal + (selectedRate?.price || 0)
                    )}
                  </span>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
