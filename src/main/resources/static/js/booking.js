document.addEventListener('DOMContentLoaded', () => {
  const form = document.querySelector('#booking-form');
  const rows = document.querySelector('#booking-items');
  const template = document.querySelector('#booking-row-template');
  const addButton = document.querySelector('#add-booking-item');
  const total = document.querySelector('#estimated-total');
  const pickupDate = document.querySelector('#pickup-date');
  if (!form || !rows || !template || !addButton || !total) return;

  const maxRows = 20;
  const indianCurrency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

  const updateRows = () => {
    const allRows = [...rows.querySelectorAll('[data-booking-row]')];
    allRows.forEach((row, index) => {
      row.querySelector('[data-row-label]').textContent = `Item ${index + 1}`;
      row.querySelector('[data-remove-row]').disabled = allRows.length === 1;
    });
    addButton.disabled = allRows.length >= maxRows;
    addButton.textContent = allRows.length >= maxRows ? 'Maximum 20 clothing types' : '＋ Add another clothing type';
  };

  const updateTotal = () => {
    const amount = [...rows.querySelectorAll('[data-booking-row]')].reduce((sum, row) => {
      const service = row.querySelector('[name="serviceIds"]');
      const quantity = Number(row.querySelector('[name="quantities"]').value || 0);
      const price = Number(service.selectedOptions[0]?.dataset.price || 0);
      return sum + (Number.isFinite(quantity) && Number.isFinite(price) ? quantity * price : 0);
    }, 0);
    total.textContent = indianCurrency.format(amount);
  };

  addButton.addEventListener('click', () => {
    if (rows.querySelectorAll('[data-booking-row]').length >= maxRows) return;
    rows.append(template.content.firstElementChild.cloneNode(true));
    updateRows();
    updateTotal();
    rows.lastElementChild.querySelector('[name="serviceIds"]').focus();
  });

  rows.addEventListener('click', event => {
    const removeButton = event.target.closest('[data-remove-row]');
    if (!removeButton || rows.querySelectorAll('[data-booking-row]').length <= 1) return;
    removeButton.closest('[data-booking-row]').remove();
    updateRows();
    updateTotal();
  });

  rows.addEventListener('input', updateTotal);
  rows.addEventListener('change', updateTotal);

  if (pickupDate) {
    const now = new Date();
    const localToday = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
    pickupDate.min = localToday;
  }

  form.addEventListener('submit', event => {
    const allRows = [...rows.querySelectorAll('[data-booking-row]')];
    const incomplete = allRows.some(row => !row.querySelector('[name="serviceIds"]').value
      || !row.querySelector('[name="clothingItemIds"]').value
      || !row.querySelector('[name="quantities"]').value);
    if (incomplete || allRows.length === 0 || allRows.length > maxRows) {
      event.preventDefault();
      form.reportValidity();
    }
  });

  updateRows();
  updateTotal();
});

