package com.kousenit.claudeai;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import static com.kousenit.claudeai.ClaudeRecords.*;

public class ContentListSerializer extends StdSerializer<ContentList> {

    public ContentListSerializer() {
        super(ContentList.class);
    }

    @Override
    public void serialize(ContentList value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.contents()); // Directly write the contents array
    }
}
