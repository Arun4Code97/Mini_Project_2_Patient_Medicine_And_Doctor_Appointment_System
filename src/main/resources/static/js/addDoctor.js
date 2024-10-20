    // Function to show the success notification and redirect
    function showSuccessAndRedirect(doctorId) {
        // Set the doctor ID in the notification box
        document.getElementById('doctorIdDisplay').textContent = doctorId;

        // Show the notification box
        document.getElementById('notificationBox').style.display = 'block';

        // Wait 5 seconds and then redirect
        setTimeout(function () {
            window.location.href = "/hospital/doctorPortal/" + doctorId;
        }, 5000);  // 5000 milliseconds = 5 seconds
    }

    // Wait for the DOM to load
    document.addEventListener("DOMContentLoaded", function() {
        let doctorId = document.getElementById('hiddenDoctorId').value;

        // Check if the doctor ID exists and call the function
        if (doctorId) {
            showSuccessAndRedirect(doctorId);
        }
    });