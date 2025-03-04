#!/bin/bash
CUST="eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJzdWIiOiAiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwgInJvbGVzIjogWyJDVVNUT01FUiJdLCAiaWF0IjogMTc3MjcxNDkxMiwgImV4cCI6IDE3NzI3MTg1MTJ9._Ztw5DS4oxtu5DeuqREfIL_cHVyHViDoetbR3x-ZT2s"
ADMIN="eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJzdWIiOiAiMjIyMjIyMjItMjIyMi0yMjIyLTIyMjItMjIyMjIyMjIyMjIyIiwgInJvbGVzIjogWyJBRE1JTiJdLCAiaWF0IjogMTc3MjcxNDkxMiwgImV4cCI6IDE3NzI3MTg1MTJ9.VbOKs7pdMh1NFNSijI_7AfxRapoHrcJK7A87OqXlwCE"
BASE="http://localhost:3004"
PASS=0
FAIL=0

check() {
  local label=$1 expected=$2 actual=$3
  if [ "$actual" = "$expected" ]; then
    echo "PASS  $label (HTTP $actual)"
    PASS=$((PASS+1))
  else
    echo "FAIL  $label (expected $expected, got $actual)"
    FAIL=$((FAIL+1))
  fi
}

echo "========== EPIC 1.1: CART MANAGEMENT =========="

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart" -H "Authorization: Bearer $CUST")
check "GET /api/cart" "200" "$CODE"

RESP=$(curl -s "$BASE/api/cart/items" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"productId\":\"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa\",\"productName\":\"Widget\",\"quantity\":2,\"unitPrice\":50.00}")
ITEM_ID=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['data']['items'][0]['id'])" 2>/dev/null)

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart/items" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"productId\":\"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab\",\"productName\":\"Widget2\",\"quantity\":1,\"unitPrice\":30.00}")
check "POST /api/cart/items" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart/items/$ITEM_ID" -X PUT -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"quantity\":3}")
check "PUT /api/cart/items/{id}" "200" "$CODE"

CART=$(curl -s "$BASE/api/cart" -H "Authorization: Bearer $CUST")
ITEM2_ID=$(echo "$CART" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['data']['items'][1]['id'])" 2>/dev/null)
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart/items/$ITEM2_ID" -X DELETE -H "Authorization: Bearer $CUST")
check "DELETE /api/cart/items/{id}" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart" -X DELETE -H "Authorization: Bearer $CUST")
check "DELETE /api/cart (clear)" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart/merge" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"sessionId\":\"test-session-123\"}")
check "POST /api/cart/merge" "200" "$CODE"

echo ""
echo "========== GUEST CART =========="

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/guest/cart/guest-sess-002")
check "GET /api/guest/cart/{sessionId}" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/guest/cart/guest-sess-002/items" -X POST -H "Content-Type: application/json" -d "{\"productId\":\"cccccccc-cccc-cccc-cccc-cccccccccccc\",\"quantity\":1}")
check "POST /api/guest/cart/{s}/items" "200" "$CODE"

echo ""
echo "========== EPIC 1.2: CHECKOUT =========="

curl -s -o /dev/null "$BASE/api/cart/items" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"productId\":\"dddddddd-dddd-dddd-dddd-dddddddddddd\",\"productName\":\"TestProd\",\"quantity\":1,\"unitPrice\":100.00}"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart/validate" -X POST -H "Authorization: Bearer $CUST")
check "POST /api/cart/validate" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/cart/totals?shippingAddressId=11111111-1111-1111-1111-111111111111" -H "Authorization: Bearer $CUST")
check "GET /api/cart/totals" "200" "$CODE"

CHECKOUT=$(curl -s "$BASE/api/orders/checkout" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"shippingAddressId\":\"11111111-1111-1111-1111-111111111111\",\"billingAddressId\":\"22222222-2222-2222-2222-222222222222\",\"paymentMethod\":\"CARD\"}")
ORDER_ID=$(echo "$CHECKOUT" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['id'])" 2>/dev/null)
if [ -n "$ORDER_ID" ]; then
  check "POST /api/orders/checkout" "201" "201"
else
  check "POST /api/orders/checkout" "201" "FAIL"
fi
echo "  Order ID: $ORDER_ID"

echo ""
echo "========== EPIC 1.3: ORDER MANAGEMENT =========="

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/orders" -H "Authorization: Bearer $CUST")
check "GET /api/orders" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/orders/$ORDER_ID" -H "Authorization: Bearer $CUST")
check "GET /api/orders/{id}" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/orders/$ORDER_ID/status" -H "Authorization: Bearer $CUST")
check "GET /api/orders/{id}/status" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/orders/$ORDER_ID/cancel" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"reason\":\"Changed my mind\"}")
check "POST /api/orders/{id}/cancel" "200" "$CODE"

echo ""
echo "========== EPIC 1.4: ADMIN =========="

# Create fresh order for admin tests
curl -s -o /dev/null "$BASE/api/cart/items" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"productId\":\"ffffffff-ffff-ffff-ffff-ffffffffffff\",\"productName\":\"AdminProd\",\"quantity\":1,\"unitPrice\":60.00}"
ADMIN_ORD=$(curl -s "$BASE/api/orders/checkout" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"shippingAddressId\":\"11111111-1111-1111-1111-111111111111\",\"billingAddressId\":\"22222222-2222-2222-2222-222222222222\",\"paymentMethod\":\"CARD\"}")
AO_ID=$(echo "$ADMIN_ORD" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['id'])" 2>/dev/null)
echo "  Admin test order: $AO_ID"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/orders" -H "Authorization: Bearer $ADMIN")
check "GET /api/admin/orders (ADMIN)" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/orders" -H "Authorization: Bearer $CUST")
check "GET /api/admin/orders (CUST=403)" "403" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/orders/$AO_ID" -H "Authorization: Bearer $ADMIN")
check "GET /api/admin/orders/{id} [NEW]" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/orders/$AO_ID/status" -X PUT -H "Authorization: Bearer $ADMIN" -H "Content-Type: application/json" -d "{\"status\":\"CONFIRMED\",\"note\":\"Payment verified\"}")
check "PUT /api/admin/orders/{id}/status [NEW]" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/orders/$AO_ID/notes" -X POST -H "Authorization: Bearer $ADMIN" -H "Content-Type: application/json" -d "{\"note\":\"VIP customer\"}")
check "POST /api/admin/orders/{id}/notes" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/orders/$AO_ID/audit-trail" -H "Authorization: Bearer $ADMIN")
check "GET /api/admin/orders/{id}/audit-trail" "200" "$CODE"

echo ""
echo "========== INTERNAL (SERVICE-TO-SERVICE) =========="

# Create fresh order for internal tests (need one in CONFIRMED state)
curl -s -o /dev/null "$BASE/api/cart/items" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"productId\":\"abababab-abab-abab-abab-abababababab\",\"productName\":\"IntProd\",\"quantity\":1,\"unitPrice\":40.00}"
INT_ORD=$(curl -s "$BASE/api/orders/checkout" -X POST -H "Authorization: Bearer $CUST" -H "Content-Type: application/json" -d "{\"shippingAddressId\":\"11111111-1111-1111-1111-111111111111\",\"billingAddressId\":\"22222222-2222-2222-2222-222222222222\",\"paymentMethod\":\"CARD\"}")
IO_ID=$(echo "$INT_ORD" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['id'])" 2>/dev/null)
echo "  Internal test order: $IO_ID"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/internal/orders/$IO_ID" -H "X-Service-Name: payment-service" -H "X-Service-Secret: internal-service-secret-key")
check "GET /internal/orders/{id}" "200" "$CODE"

# PENDING -> CONFIRMED
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/internal/orders/$IO_ID/status" -X PUT -H "X-Service-Name: payment-service" -H "X-Service-Secret: internal-service-secret-key" -H "Content-Type: application/json" -d "{\"status\":\"CONFIRMED\",\"note\":\"Payment confirmed\"}")
check "PUT /internal status PENDING->CONFIRMED" "200" "$CODE"

# CONFIRMED -> PROCESSING
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/internal/orders/$IO_ID/status" -X PUT -H "X-Service-Name: fulfillment-service" -H "X-Service-Secret: internal-service-secret-key" -H "Content-Type: application/json" -d "{\"status\":\"PROCESSING\",\"note\":\"Processing\"}")
check "PUT /internal status CONFIRMED->PROCESSING" "200" "$CODE"

# PROCESSING -> SHIPPED
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/internal/orders/$IO_ID/status" -X PUT -H "X-Service-Name: shipping-service" -H "X-Service-Secret: internal-service-secret-key" -H "Content-Type: application/json" -d "{\"status\":\"SHIPPED\",\"note\":\"Shipped\",\"trackingNumber\":\"TRK123456\"}")
check "PUT /internal status PROCESSING->SHIPPED" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/internal/orders/$IO_ID/shipping-details" -H "X-Service-Name: shipping-service" -H "X-Service-Secret: internal-service-secret-key")
check "GET /internal/orders/{id}/shipping" "200" "$CODE"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/internal/orders/$IO_ID")
check "GET /internal (no auth=403)" "403" "$CODE"

echo ""
echo "=========================================="
echo "RESULTS: $PASS passed, $FAIL failed"
echo "=========================================="
