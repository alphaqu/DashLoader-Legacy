package net.oskarstrom.dashloader.data.serialization;

import io.activej.codegen.expression.Expression;
import io.activej.serializer.SerializerDef;
import io.activej.serializer.impl.AbstractSerializerDefMap;
import io.activej.serializer.impl.SerializerDefInt;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.function.Function;

import static io.activej.codegen.expression.Expressions.forEach;

public class SerializerDefInt2ObjectMap extends AbstractSerializerDefMap {

	public SerializerDefInt2ObjectMap(SerializerDef valueSerializer) {
		super(
				new SerializerDefInt(false, true),
				valueSerializer,
				Int2ObjectMap.class, Int2ObjectLinkedOpenHashMap.class,
				int.class, Object.class,
				false
		);
	}

	@Override
	public Expression mapForEach(Expression collection, Function<Expression, Expression> forEachKey, Function<Expression, Expression> forEachValue) {
		return forEach(collection, forEachKey, forEachValue);
	}

	@Override
	public SerializerDef ensureNullable() {
		return new SerializerDefInt2ObjectMap(valueSerializer);
	}
}
