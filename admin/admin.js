const API_URL = "http://localhost:8080/admin/texts";

async function loadTexts() {
    const response = await fetch(API_URL);
    const texts = await response.json();
    const tableBody = document.getElementById("textsTableBody");

    tableBody.innerHTML = "";
    texts.forEach(text => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${text.id}</td>
            <td>${text.content}</td>
            <td>${text.difficulty}</td>
            <td>
                <button onclick="editText(${text.id}, '${text.content}', '${text.difficulty}')">Edit</button>
                <button onclick="deleteText(${text.id})">Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

document.getElementById("addTextForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const content = document.getElementById("textContent").value;
    const difficulty = document.getElementById("textDifficulty").value;

    await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content, difficulty })
    });

    document.getElementById("textContent").value = "";
    loadTexts();
});

async function deleteText(id) {
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    loadTexts();
}

function editText(id, content, difficulty) {
    document.getElementById("editTextId").value = id;
    document.getElementById("editTextContent").value = content;
    document.getElementById("editTextDifficulty").value = difficulty;
    document.getElementById("editTextForm").classList.remove("hidden");
}

function cancelEdit() {
    document.getElementById("editTextForm").classList.add("hidden");
}

document.getElementById("editTextForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const id = document.getElementById("editTextId").value;
    const content = document.getElementById("editTextContent").value;
    const difficulty = document.getElementById("editTextDifficulty").value;

    await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content, difficulty })
    });

    document.getElementById("editTextForm").classList.add("hidden");
    loadTexts();
});

loadTexts();
