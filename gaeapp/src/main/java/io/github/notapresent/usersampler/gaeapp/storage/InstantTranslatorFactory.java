package io.github.notapresent.usersampler.gaeapp.storage;

import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.translate.CreateContext;
import com.googlecode.objectify.impl.translate.LoadContext;
import com.googlecode.objectify.impl.translate.SaveContext;
import com.googlecode.objectify.impl.translate.SkipException;
import com.googlecode.objectify.impl.translate.TypeKey;
import com.googlecode.objectify.impl.translate.ValueTranslator;
import com.googlecode.objectify.impl.translate.ValueTranslatorFactory;
import java.time.Instant;
import java.util.Date;


public class InstantTranslatorFactory extends ValueTranslatorFactory<Instant, Date> {

  public InstantTranslatorFactory() {
    super(Instant.class);
  }

  @Override
  protected ValueTranslator<Instant, Date> createValueTranslator(TypeKey<Instant> tk,
      CreateContext ctx, Path path) {
    final Class<?> clazz = tk.getTypeAsClass();

    return new ValueTranslator<Instant, Date>(Date.class) {
      @Override
      protected Instant loadValue(Date value, LoadContext ctx, Path path) throws SkipException {
        return value.toInstant();
      }

      @Override
      protected Date saveValue(Instant value, boolean index, SaveContext ctx, Path path)
          throws SkipException {
        return Date.from(value);
      }
    };
  }
}