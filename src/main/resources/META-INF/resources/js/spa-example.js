function closeModal(elementId) {
  document.getElementById(elementId).removeAttribute("open");
}

function openModal(elementId) {
  document.getElementById(elementId).setAttribute("open", "open");
}

function resetTodoForm() {
  document.getElementById("id").value = 0;
  document.getElementById("summary").value = "";
  document.getElementById("description").value = "";
}

function focusElement(elementId) {
  window.setTimeout(function() {
    document.getElementById(elementId).focus();
  }, 0);
}