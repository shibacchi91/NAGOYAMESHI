const stripe = Stripe('pk_test_51P53pqDtHZHUakkXLCgbmLtVbltxdGC04uCGD5uPSMVD2wqGfBwUxf5UxOMwV7yJErvxbjnkg2SlEPNWRNtc8DfQ006DE8sfuE');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });