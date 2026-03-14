async function run() {
  const loginRes = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ identifier: 'prateekagrawal@revworkforce.com', password: 'Welcome@123' })
  });
  const loginData = await loginRes.json();
  console.log("LOGIN TOKEN:", loginData.token ? "SUCCESS" : "FAIL");

  if (!loginData.token) return;

  const notifRes = await fetch('http://localhost:8080/api/notifications', {
    headers: { 'Authorization': `Bearer ${loginData.token}` }
  });
  console.log("NOTIFICATIONS STATUS:", notifRes.status);
  const notifData = await notifRes.text();
  console.log("NOTIFICATIONS BODY:", notifData);
}

run();
