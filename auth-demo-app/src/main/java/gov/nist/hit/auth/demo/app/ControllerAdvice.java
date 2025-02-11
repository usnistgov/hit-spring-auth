package gov.nist.hit.auth.demo.app;

import gov.nist.hit.auth.demo.model.AckStatus;
import gov.nist.hit.auth.demo.model.OpAck;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public OpAck<Void> handleException(Exception ex) {
		return new OpAck<>(AckStatus.FAILED, ex.getMessage(), "", null);
	}
}
