document.addEventListener('DOMContentLoaded', () => {
  const service = document.querySelector('#serviceId');
  const quantity = document.querySelector('#quantity');
  const total = document.querySelector('#estimated-total');
  if (!service || !quantity || !total) return;
  const update = () => {
    const price = Number(service.selectedOptions[0]?.dataset.price || 0);
    const count = Math.max(0, Number(quantity.value || 0));
    total.textContent = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(price * count);
  };
  service.addEventListener('change', update);
  quantity.addEventListener('input', update);
  update();
});
