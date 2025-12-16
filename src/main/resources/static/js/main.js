fetch("/cart")
  .then(response => response.json())
  .then(data => {
    console.log(data); // Render cart
  })
  .catch(err => console.error(err));
