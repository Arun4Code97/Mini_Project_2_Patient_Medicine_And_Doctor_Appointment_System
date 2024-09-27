       document.addEventListener('DOMContentLoaded', function() {
            var datePicker = document.getElementById('datePicker');

                // Event listener to trigger when a new date is selected
                datePicker.addEventListener('change', function() {
                    var selectedDate = this.value; // Get the selected date in yyyy-MM-dd format
                    var url = new URL(window.location.href);
                    // Update the 'date' query parameter in the URL
                    url.searchParams.set('date', selectedDate);

                    // Reload the page with the new date in the URL
                    window.location.href = url;
                });

        });

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

