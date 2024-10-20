    // Function to show the success notification and redirect
    function showSuccessAndRedirect(patientId) {
        // Set the patient ID in the notification box
        document.getElementById('patientIdDisplay').textContent = patientId;

        // Show the notification box
        document.getElementById('notificationBox').style.display = 'block';

        // Wait 5 seconds and then redirect
        setTimeout(function () {
            window.location.href = "/hospital/patientPortal/" + patientId;
        }, 5000);  // 5000 milliseconds = 5 seconds
    }

    // Wait for the DOM to load
    document.addEventListener("DOMContentLoaded", function() {
        let patientId = document.getElementById('hiddenPatientId').value;

        // Check if the patient ID exists and call the function
        if (patientId) {
            showSuccessAndRedirect(patientId);
        }
    });