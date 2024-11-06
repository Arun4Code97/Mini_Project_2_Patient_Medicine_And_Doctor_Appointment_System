package com.healthcare.doctor_consultation_medicine.Exception;

import com.healthcare.doctor_consultation_medicine.Others.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(
            NoResourceFoundException ex, Model model) {

        // Capture stack trace as a String for debugging purpose
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Create the ApiError object
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Wrong URL , Please check entered end points",
                stackTrace
        );
        model.addAttribute("error",apiError);

        return "common/error";
    }
    @ExceptionHandler( PatientNotFoundException.class )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerMethodForResourceNotFoundException(PatientNotFoundException ex, Model model)
    {
        // Capture stack trace as a String for debugging purpose
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Create the ApiError object
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                stackTrace
        );
        model.addAttribute("error",apiError);
        return "common/error";
    }
    @ExceptionHandler( DoctorNotFoundException.class )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerMethodForResourceNotFoundException(DoctorNotFoundException ex, Model model)
    {
        // Capture stack trace as a String for debugging purpose
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Create the ApiError object
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                stackTrace
        );
        model.addAttribute("error",apiError);
        return "common/error";
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,Model model) {

        // Capture stack trace as a String for debugging purpose
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Create the ApiError object
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid argument type: " + ex.getName(),
                stackTrace
        );
        model.addAttribute("error",apiError);

        return "common/error";
    }
}
