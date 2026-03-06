import { useEffect, useState } from 'react'
import axios from 'axios'
import { jsPDF } from "jspdf"

function StatCard({ title, value, color }) {
  const colors = {
    blue: 'border-blue-500',
    purple: 'border-purple-500',
    yellow: 'border-yellow-500',
    green: 'border-green-500'
  };
  return (
    <div className={`bg-slate-900 p-6 rounded-2xl border-b-4 ${colors[color]} shadow-xl`}>
      <p className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-1">{title}</p>
      <h2 className="text-3xl font-black text-white tracking-tight">{value}</h2>
    </div>
  );
  
}

function App() {
  const [activeTab, setActiveTab] = useState('overview');
  const [shippingList, setShippingList] = useState([]);
  const [formData, setFormData] = useState({
    orderId: '', destination: '', weight: '', distance: '', carrier: 'FedEx'
  });
  const [calculatedRate, setCalculatedRate] = useState(null);

  useEffect(() => {
    fetchShipments();
  }, []);

  const fetchShipments = () => {
    axios.get('http://localhost:8080/api/shipping')
      .then(res => setShippingList(res.data))
      .catch(err => console.error(err));
  };

  const handleCalculateRate = () => {
    const carrierFactor = formData.carrier === 'FedEx' ? 0.5 : formData.carrier === 'DHL' ? 0.7 : 0.4;
    const rate = (formData.weight * formData.distance * carrierFactor).toFixed(2);
    setCalculatedRate(rate);
  };

  const generatePDFLabel = (data) => {
    const doc = new jsPDF();
    doc.setFontSize(22);
    doc.text("SHOPSPHERE SHIPPING LABEL", 20, 20);
    doc.line(20, 25, 190, 25);

    doc.setFontSize(14);
    doc.text(`Tracking ID: ${data.trackingId}`, 20, 40);
    doc.text(`Carrier: ${data.carrier}`, 20, 50);
    doc.text(`Order Ref: ${data.orderId}`, 20, 60);
    doc.text(`Destination: ${data.destination}`, 20, 70);
    doc.text(`Weight: ${data.weight} KG`, 20, 80);
    doc.text(`Total Cost: $${calculatedRate}`, 20, 90);

    doc.rect(20, 100, 50, 20);
    doc.text("BARCODE", 25, 112);

    doc.save(`label_${data.trackingId}.pdf`);
  };

  const handleAddShipment = async (e) => {
    e.preventDefault();

    // Tracking ID එක හදාගමු
    const trackingId = `SHP-${Math.floor(1000000000000 + Math.random() * 9000000000000)}`;
    const newShipment = {
      ...formData,
      status: 'Label Generated',
      trackingNumber: trackingId // ඔයාගේ Controller එකේ තියෙන්නේ trackingNumber කියලා
    };

    try {
      // 1. Shipment එක Create කිරීම
      // URL එක බලන්න: /api/shipping/create-label
      console.log("Creating shipment...");
      await axios.post('http://localhost:8080/api/shipping/create-label', newShipment);

      // 2. PDF එක Download කිරීම
      // URL එක බලන්න: /api/shipping/download-label/{trackingNumber}
      console.log("Downloading label for: ", trackingId);
      const response = await axios.get(`http://localhost:8080/api/shipping/download-label/${trackingNumber}`, {
        responseType: 'blob',
      });

      // 3. Browser එකේ Download කරවීම
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Label-${trackingId}.pdf`);
      document.body.appendChild(link);
      link.click();

      // පිරිසිදු කිරීම්
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      alert("Success! Label Downloaded.");
      fetchShipments(); // Table එක refresh කරන්න
      setActiveTab('overview');

    } catch (error) {
      console.error("Full Error Details:", error.response); // මෙතැනින් console එකේ විස්තර බලාගන්න
      alert("Error: " + (error.response?.status === 404 ? "Check your Endpoint Paths!" : "Connection Problem"));
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 p-8 font-sans">
      <header className="max-w-7xl mx-auto flex justify-between items-center mb-10">
        <h1 className="text-3xl font-black italic text-blue-500 tracking-tighter uppercase">SHOPSPHERE LOGISTICS</h1>
        <div className="bg-slate-900 p-1 rounded-xl flex gap-2 border border-slate-800">
          <button onClick={() => setActiveTab('overview')} className={`px-4 py-2 rounded-lg transition-all ${activeTab === 'overview' ? 'bg-blue-600 text-white' : 'hover:bg-slate-800'}`}>Overview</button>
          <button onClick={() => setActiveTab('create')} className={`px-4 py-2 rounded-lg transition-all ${activeTab === 'create' ? 'bg-blue-600 text-white' : 'hover:bg-slate-800'}`}>Create Shipment</button>
        </div>
      </header>

      <main className="max-w-6xl mx-auto">
        {activeTab === 'create' && (
          <div className="max-w-4xl mx-auto bg-slate-900 p-8 rounded-3xl border border-slate-800 shadow-2xl">
            <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">📦 New Shipment & Calculator</h2>
            <form onSubmit={handleAddShipment} className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold text-slate-500 uppercase">Order Reference</label>
                <input required className="bg-slate-800 p-3 rounded-xl border border-slate-700 focus:border-blue-500 outline-none" placeholder="ORD-XXXX" value={formData.orderId} onChange={(e) => setFormData({...formData, orderId: e.target.value})} />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold text-slate-500 uppercase">Carrier Partner</label>
                <select className="bg-slate-800 p-3 rounded-xl border border-slate-700 outline-none" value={formData.carrier} onChange={(e) => setFormData({...formData, carrier: e.target.value})}>
                  <option>FedEx</option><option>DHL</option><option>UPS</option>
                </select>
              </div>
              <div className="col-span-1 md:col-span-2 flex flex-col gap-2">
                <label className="text-xs font-bold text-slate-500 uppercase">Destination Address</label>
                <input required className="bg-slate-800 p-3 rounded-xl border border-slate-700 outline-none focus:border-blue-500" placeholder="Street, City, Country" value={formData.destination} onChange={(e) => setFormData({...formData, destination: e.target.value})} />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold text-slate-500 uppercase">Distance (KM)</label>
                <input required type="number" className="bg-slate-800 p-3 rounded-xl border border-slate-700 outline-none focus:border-blue-500" placeholder="0" value={formData.distance} onChange={(e) => setFormData({...formData, distance: e.target.value})} />
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-xs font-bold text-slate-500 uppercase">Weight (KG)</label>
                <input required type="number" className="bg-slate-800 p-3 rounded-xl border border-slate-700 outline-none focus:border-blue-500" placeholder="0" value={formData.weight} onChange={(e) => setFormData({...formData, weight: e.target.value})} />
              </div>

              <button type="button" onClick={handleCalculateRate} className="col-span-1 md:col-span-2 bg-slate-700 hover:bg-slate-600 p-4 rounded-xl font-bold transition-all text-blue-400">Calculate Rate</button>

              {calculatedRate && (
                <div className="col-span-1 md:col-span-2 bg-blue-600/10 border border-blue-500/50 p-6 rounded-2xl flex flex-col md:flex-row justify-between items-center gap-4">
                  <div>
                    <p className="text-xs font-bold text-blue-400 uppercase tracking-widest">Estimated Cost</p>
                    <h3 className="text-4xl font-black text-white">${calculatedRate}</h3>
                  </div>
                  <button type="submit" className="w-full md:w-auto bg-blue-600 hover:bg-blue-500 px-8 py-4 rounded-xl font-bold shadow-lg shadow-blue-900/40 transition-all text-white">Generate Label & Post</button>
                </div>
              )}
            </form>
          </div>
        )}

        {activeTab === 'overview' && (
          <section className="space-y-8">
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
              <StatCard title="Total Shipments" value={shippingList.length} color="blue" />
              <StatCard title="FedEx Active" value="12" color="purple" />
              <StatCard title="DHL Active" value="08" color="yellow" />
              <StatCard title="UPS Active" value="05" color="green" />
            </div>

            <div className="bg-slate-900 rounded-3xl border border-slate-800 overflow-hidden shadow-2xl">
              <div className="p-6 border-b border-slate-800">
                <h3 className="text-xl font-bold">Live Shipping Status</h3>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-left">
                  <thead className="bg-slate-800/50 text-slate-400 text-xs uppercase tracking-wider">
                    <tr>
                      <th className="p-4">Tracking ID</th>
                      <th className="p-4">Carrier</th>
                      <th className="p-4">Destination</th>
                      <th className="p-4">Status</th>
                      <th className="p-4 text-right">Label</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800">
                    {shippingList.length > 0 ? shippingList.map((item) => (
                      <tr key={item.id} className="hover:bg-slate-800/30 transition-colors">
                        <td className="p-4 font-mono text-cyan-400 font-bold">{item.trackingId}</td>
                        <td className="p-4 font-bold text-slate-300">{item.carrier}</td>
                        <td className="p-4 text-sm">{item.destination}</td>
                        <td className="p-4">
                          <span className="px-3 py-1 bg-green-500/10 text-green-500 border border-green-500/20 rounded-full text-xs font-bold uppercase">
                            {item.status}
                          </span>
                        </td>
                        <td className="p-4 text-right">
                          <button onClick={() => generatePDFLabel(item)} className="text-blue-400 hover:text-blue-300 text-sm font-bold transition-all underline">Download PDF</button>
                        </td>
                      </tr>
                    )) : (
                      <tr>
                        <td colSpan="5" className="p-10 text-center text-slate-500 italic">No shipments found. Create one to see data.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </section>
        )}
      </main>
    </div>
  )
}

export default App;