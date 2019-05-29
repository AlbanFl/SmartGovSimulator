package smartgov.core.output.coordinate;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CoordinateSerializer extends StdSerializer<Coordinate> {

	private static final long serialVersionUID = 1L;

	public CoordinateSerializer() {
		this(null);
	}
	
	protected CoordinateSerializer(Class<Coordinate> t) {
		super(t);
	}

	@Override
	public void serialize(Coordinate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		double[] coordinates = { value.x, value.y };
		gen.writeObject(coordinates);
	}

}
