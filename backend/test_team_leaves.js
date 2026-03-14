async function run() {
  const loginRes = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ identifier: 'prateekagrawal@revworkforce.com', password: 'Welcome@123' })
  });
  const loginData = await loginRes.json();
  console.log("LOGIN TOKEN:", loginData.token ? "SUCCESS" : "FAIL");

  if (!loginData.token) return;

  const leavesRes = await fetch('http://localhost:8080/api/manager/leaves/team', {
    headers: { 'Authorization': `Bearer ${loginData.token}` }
  });
  console.log("TEAM LEAVES STATUS:", leavesRes.status);
  const leavesData = await leavesRes.text();
  console.log("TEAM LEAVES BODY:", leavesData);
}

run();
