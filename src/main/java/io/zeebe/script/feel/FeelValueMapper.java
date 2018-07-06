package io.zeebe.script.feel;

import java.util.stream.Collectors;

import org.camunda.feel.interpreter.DefaultContext;
import org.camunda.feel.interpreter.Val;
import org.camunda.feel.interpreter.ValContext;
import org.camunda.feel.interpreter.ValList;
import org.camunda.feel.spi.CustomValueMapper;

import scala.collection.JavaConverters;

public class FeelValueMapper extends CustomValueMapper {

  @Override
  public Object unpackVal(Val value) {

    if (value instanceof ValList) {
      final ValList list = (ValList) value;
      return JavaConverters.seqAsJavaList(list.items())
          .stream()
          .map(this::unpackVal)
          .collect(Collectors.toList());

    } else if (value instanceof ValContext) {
      final ValContext context = (ValContext) value;

      if (context.context() instanceof DefaultContext) {
        final DefaultContext dc = (DefaultContext) context.context();
        return JavaConverters.mapAsJavaMap(dc.variables())
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey(), e -> unpackVal(toVal(e.getValue()))));
      }
    }

    return super.unpackVal(value);
  }
}
