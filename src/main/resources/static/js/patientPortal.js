    document.addEventListener('DOMContentLoaded', function () {

    const form = document.getElementById('patientForm');
<!--        var mode = /*[[${mode}]]*/ 'add'; Not working-->
        var mode = document.getElementById("modeHiddenInput").value;

        if (mode === 'add') {
            form.action = '/hospital/addPatient';
            form.method = 'post' ;
               }
        if (mode === 'update') {
             var input = document.getElementById('idInput');
             input.disabled = true;

            const patientId = /*[[${patient.id}]]*/ 0;
            form.action = '/hospital/patientPortal/updateProfile/' + patientId;
            form.method = 'post' ;
        }
        if (mode === 'view') {
             var inputs = document.querySelectorAll('#patientForm input');
            // Disable each input
                inputs.forEach(function(input) {
                    input.disabled = true;
                });

            var textAreas = document.querySelectorAll('#patientForm textarea');
                    textAreas.forEach(function(textarea) {
                        textarea.disabled = true;
                        });

            form.method = 'get';
            const patientId = /*[[${patient.id}]]*/ 0;
            form.action = '/hospital/patientPortal/' + patientId;
        }


});

    function confirmDelete() {
                   document.getElementById('submitDelete').click();
                       }

//                           <form class="form-inline" th:object="${patient}" method="post" id="patientForm">

//    <form class="form-inline" th:object="${patient}"  th:action="@{/hospital/patientPortal/updateProfile/{id}(id=${patient.id}) }" method="post" id="patientForm">
//        <!-- Your form fields go here -->
//        <input type="hidden" id="modeHiddenInput" th:value="${mode}" />
//        <input type="hidden"  th:if="${mode == 'update'}" name="_method" value="put"/>
