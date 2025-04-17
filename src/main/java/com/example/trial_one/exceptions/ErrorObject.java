package com.example.trial_one.exceptions;

import lombok.Data;

@Data
public class ErrorObject {
	private Integer statusCode;
	private String message;
	private java.util.Date timestamp;
}
