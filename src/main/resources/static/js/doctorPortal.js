document.addEventListener('DOMContentLoaded', function() {

    var modeInput = document.getElementById('modeHiddenInput');
    var doctorIdInput = document.getElementById('idInput');

      if(modeInput && doctorIdInput){
        const mode = modeInput.value;
        const doctorId = doctorIdInput.value;
        const form = document.getElementById('doctorForm');
        const imageFileInput = document.getElementById('imageFile');

            if (mode === 'add') {
            // Prepare form data
            const formData = new FormData(form);
            const imageFile = imageFileInput. files[0];  // Get the selected file

            formData.append("imageFile", imageFile);
                form.action = '/hospital/addDoctor';
                form.method = 'post';
            }
            if (mode === 'update') {
            // Prepare form data
            const formData = new FormData(form);
            const imageFile = imageFileInput. files[0];  // Get the selected file

            formData.append("imageFile", imageFile);
<!--              var input = document.getElementById('idInput');-->
                doctorIdInput.disabled = true;
                form.method = 'post';
                form.action = '/hospital/doctorPortal/updateProfile/' + doctorId;
            }

            if (mode === 'view') {
              console.log("Reaching view in js");
               var inputs = document.querySelectorAll('#doctorForm input');
              // Disable each input
                  inputs.forEach(function(input) {
                      input.disabled = true;
                  });

              var textAreas = document.querySelectorAll('#doctorForm textarea');
                      textAreas.forEach(function(textarea) {
                          textarea.disabled = true;
                          });
              form.method = 'get';
              form.action = '/hospital/doctorPortal/' + doctorId;
            }
      }else {
          console.log("Mode and PatientId are not found in the DOM");
       }

           var datePicker = document.getElementById('datePicker');

        if(datePicker) {
            // Event listener to trigger when a new date is selected
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
});

    function clickBookButton(button,slotTime) {
    document.getElementById("slotTime").value = slotTime;

    // Reset styles of all buttons to their default state
    var buttons = document.querySelectorAll('button.booked');
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
            function confirmDelete() {
                   document.getElementById('submitDelete').click();
    }
