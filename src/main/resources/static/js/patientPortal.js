 document.addEventListener('DOMContentLoaded', function () {
        var modeInput = document.getElementById('modeHiddenInput');
        var patientIdInput = document.getElementById('idInput');
        var datePicker = document.getElementById('datePicker');

       if(datePicker) {// Event listener to trigger when a new date is selected
             datePicker.addEventListener('change', function() {
                    var selectedDate = this.value; // Get the selected date in yyyy-MM-dd format
                    var url = new URL(window.location.href);
                    // Update the 'date' query parameter in the URL
                    url.searchParams.set('date', selectedDate);

                    // Reload the page with the new date in the URL
                    window.location.href = url;
              });
       } else {
             console.log("Date picker not found in the DOM");
         }
//------------------3-oct night -- console log need set up some if exist else console.log
    if(modeInput && patientIdInput){
        const form = document.getElementById('patientForm');
            var mode = modeInput.value;
            var patientId = patientIdInput.value;

        if (mode === 'add') {

            form.action = '/hospital/addPatient';
            form.method = 'post' ;
               }


        if (mode === 'update') {
                var input = document.getElementById('idInput');
             input.disabled = true;

            form.action = '/hospital/patientPortal/updateProfile/' + patientId;
            form.method = 'post' ;
        }
        if (mode === 'view') {
             var inputs = document.querySelectorAll('input');
            // Disable each input
                inputs.forEach(function(input) {
//                    console.log("Disabling input: " + input.name);  // Debugging each input
                    input.disabled = true;
                });

            var textAreas = document.querySelectorAll('textarea');
                    textAreas.forEach(function(textarea) {
//                        console.log("Disabling textarea: " + textarea.name);  // Debugging each textarea
                        textarea.disabled = true;
                        });

            form.method = 'get';
            form.action = '/hospital/patientPortal/' + patientId;
        }

    }else {
          console.log("Mode and PatientId are not found in the DOM");
      }

});

    function confirmDelete() {
                   document.getElementById('submitDelete').click();
    }

    function clickBookButton(button,slotTime) {
                document.getElementById("slotTime").value = slotTime;

                // Reset styles of all buttons to their default state
                var buttons = document.querySelectorAll('button.available');
                    buttons.forEach(btn => {
                        btn.style.backgroundColor = ''; // Reset to default
                        btn.style.color = '';           // Reset to default
                    });

                // Apply the clicked state to the current button
                button.style.backgroundColor = '#4CAF50';  // Change to your desired clicked color
                button.style.color = 'white';              // Change text color
        }

    function validateScheduleAppointmentForm(event) {
                var slotTime = document.getElementById('slotTime').value;

                if (!slotTime) {
                    // Display error message if no time slot is selected
                    document.getElementById('errorMessage').style.display = 'block';
                    event.preventDefault(); // Prevent form submission
                    return false;
                }

                return true; // Continue form submission if validation passes
    }

