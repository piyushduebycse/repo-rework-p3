const options = {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    identifier: 'admin@revworkforce.com',
    password: 'password'
  })
};

fetch('http://localhost:8080/api/auth/login', options)
  .then(res => res.json())
  .then(data => console.log(data.message))
  .catch(err => console.error(err));
