   function showAddMedicineModal() {
     console.log("Add Medicine Modal triggered");
               const modal = document.getElementById('addMedicineModal');
            if (modal) {
                modal.style.display = 'block';
            } else {
                console.log("Modal with ID 'addMedicineModal' not found!");
            }
    }

    function showUpdateMedicineModal(medicineId) {
        // Populate the modal form with the selected medicine data via AJAX
        // Then display the modal
        fetch(`/medicine/get/${medicineId}`)
            .then( response => response.json() )
            .then(data => {
                 document.getElementById('medicineId').value = data.id;
                document.getElementById('medicineName').value = data.name;
                document.getElementById('medicineDosage').value = data.dosage;
                document.getElementById('medicineDuration').value = data.duration;
                document.getElementById('updateMedicineModal').style.display = 'block';
            });

    }

    function deleteMedicine(medicineId) {
        // Confirm and then delete the medicine via AJAX
        if (confirm('Are you sure you want to delete this medicine?')) {
            fetch(`/deleteMedicine/${medicineId}`, { method: 'GET' })
                .then(response => {
                    if (response.ok) {
                        // Refresh the table or remove the deleted row from the DOM
                        location.reload();
                    }
                    else {
                        // Handle errors if needed
                        alert('Failed to delete the medicine. Please try again.');
                    }
                })
                .catch(error => {
                         console.log('Error:', error);
                         alert('An error occurred while deleting the medicine.');
                });
        }
    }