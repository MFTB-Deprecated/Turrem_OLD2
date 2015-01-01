package net.turrem.app.tile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.turrem.app.mod.registry.Registrable;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Registrable
public @interface RegisterTile
{
	public String id();
}
