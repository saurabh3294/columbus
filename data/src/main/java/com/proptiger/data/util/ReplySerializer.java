package com.proptiger.data.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.proptiger.data.model.ProjectDiscussion.Replies;

public class ReplySerializer extends JsonSerializer<Replies> {
	
	@Override
	public void serialize(Replies reply, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException{
		switch( reply ){
			case F:
				//jgen.writeString(Boolean.FALSE.toString());
				jgen.writeBoolean(Boolean.FALSE);
				break;
			case T:
				jgen.writeBoolean(Boolean.TRUE);
				//jgen.writeString(Boolean.TRUE.toString());
				break;
		}
		return;
		//jgen.writeString(Boolean.FALSE.toString());
	}
		
}