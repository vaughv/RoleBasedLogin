const token = localStorage.getItem("jwtToken");

fetch("http://localhost:8080/api/teacher/courses", {
    method: "GET",
    headers: {
        "Authorization": "Bearer " + token,
        "Content-Type": "application/json"
    }
})
    .then(response => {
        if (response.status === 401) {
            // Not authorized — maybe token is missing or expired
            alert("Session expired. Please log in again.");
            window.location.href = "login.html";
        }
        return response.json();
    })
    .then(data => {
        // Do something with the protected data
    })
    .catch(error => console.error("Error fetching protected resource", error));


window.onload = () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("Please log in first.");
        window.location.href = "login.html";
    }
};
