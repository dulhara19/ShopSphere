let stream = null;

function byId(id) {
  return document.getElementById(id);
}

function base() {
  return byId("baseUrl").value.replace(/\/$/, "");
}

function val(id) {
  return byId(id).value.trim();
}

function num(id, fallback = 0) {
  const parsed = Number(byId(id).value);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function pretty(data) {
  if (typeof data === "string") return data;
  return JSON.stringify(data, null, 2);
}

function out(title, data) {
  byId("output").textContent = title + "\n" + pretty(data);
}

function streamLog(msg) {
  const el = byId("streamLog");
  el.textContent += `[${new Date().toISOString()}] ${msg}\n`;
  el.scrollTop = el.scrollHeight;
}

async function req(path, method = "GET", body = null, contentType = "application/json") {
  const opts = { method, headers: {} };
  if (body !== null) {
    if (contentType) opts.headers["Content-Type"] = contentType;
    opts.body = contentType === "application/json" ? JSON.stringify(body) : body;
  }

  const res = await fetch(base() + path, opts);
  const text = await res.text();
  let data = text;
  try { data = text ? JSON.parse(text) : ""; } catch (_) {}
  if (!res.ok) throw { status: res.status, data };
  return data;
}

function wire(id, handler) {
  byId(id).addEventListener("click", async () => {
    try {
      const data = await handler();
      out(id + " OK", data);
    } catch (e) {
      out(id + " ERROR", e);
    }
  });
}

wire("btnHealth", () => req("/actuator/health"));
wire("btnLowStock", () => req("/inventory/low-stock"));
wire("btnWarehouses", () => req("/warehouses"));

wire("btnCreateInventory", () => req("/inventory", "POST", {
  product_id: val("productId"),
  quantity: num("quantity", 0),
  low_stock_threshold: num("threshold", 5)
}));
wire("btnGetInventory", () => req(`/inventory/${val("productId")}`));
wire("btnUpdateInventory", () => req(`/inventory/${val("productId")}`, "PUT", {
  quantity: num("quantity", 0),
  low_stock_threshold: num("threshold", 5)
}));
wire("btnDeleteInventory", () => req(`/inventory/${val("productId")}`, "DELETE"));

wire("btnReserve", () => req("/inventory/reserve", "POST", {
  product_id: val("productId"),
  quantity: num("quantity", 1),
  order_id: val("orderId") || null
}));
wire("btnConfirm", () => req("/inventory/confirm", "POST", {
  reservationId: val("reservationId")
}));
wire("btnRelease", () => req("/inventory/release", "POST", {
  reservationId: val("reservationId")
}));
wire("btnCheckAvailability", () => req("/inventory/check-availability", "POST", {
  product_id: val("productId"),
  quantity: num("quantity", 1)
}));

wire("btnCreateWarehouse", () => req("/warehouses", "POST", {
  code: val("warehouseCode"),
  name: val("warehouseName"),
  location: "Default Location",
  address: "Default Address"
}));
wire("btnAssignWarehouse", () => req(`/inventory/${val("productId")}/warehouses/${val("warehouseId")}`, "POST", {
  quantity: num("quantity", 0)
}));
wire("btnWarehouseStock", () => req(`/inventory/${val("productId")}/warehouses`));
wire("btnTransfer", () => req("/inventory/transfer", "POST", {
  product_id: val("productId"),
  from_warehouse_id: val("warehouseId"),
  to_warehouse_id: val("toWarehouseId"),
  quantity: num("quantity", 1)
}));

wire("btnTurnover", () => req("/inventory/analytics/turnover"));
wire("btnDaysRemaining", () => req("/inventory/analytics/days-remaining"));
wire("btnForecasts", () => req("/inventory/forecasts"));
wire("btnHistory", () => req(`/inventory/${val("productId")}/history`));
wire("btnAuditCsv", () => req("/inventory/audit-report"));
wire("btnExportCsv", () => req("/inventory/export"));
wire("btnRegisterWebhook", () => req("/inventory/webhooks", "POST", {
  url: "http://localhost:9999/webhook-test",
  eventType: "STOCK_UPDATED"
}));

byId("btnConnectStream").addEventListener("click", () => {
  if (stream) stream.close();
  stream = new EventSource(base() + "/inventory/stream");
  stream.onopen = () => streamLog("Connected");
  stream.onerror = () => streamLog("Stream error");
  stream.onmessage = (e) => streamLog(e.data);
  stream.addEventListener("stock-event", (e) => streamLog("stock-event " + e.data));
});

byId("btnStopStream").addEventListener("click", () => {
  if (stream) {
    stream.close();
    stream = null;
    streamLog("Stopped");
  }
});

byId("saveBaseBtn").addEventListener("click", () => {
  out("Base URL", base());
});
