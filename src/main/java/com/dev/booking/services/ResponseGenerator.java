package com.dev.booking.services;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {
	public static ResponseEntity<String> generateJsonResponse(int code, Object object, String error) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		if(object != null) {
			JSONObject cmt = new JSONObject(object);
			json.put("result", cmt);
		}
		if(error != null || error != "") {
			json.put("error", error);
		}
		HttpStatus status = code == 200 ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		
		return ResponseEntity
				.status(status)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(json.toString());
	}
}
