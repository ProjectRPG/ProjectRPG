package rpg.project.lib.internal.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Functions<X, Y> {
	
	public static <X, Y> Function<X, Y> memoize(Function<X, Y> func) {
		Map<X, Y> cache = new ConcurrentHashMap<X, Y>();
		
		return (a) -> cache.computeIfAbsent(a, func);
	}
	
	/**<p>This function accepts two like objects to be consumed based on the
	 * relationship between them.  The supplied BiConsumers are then chosen
	 * based on the ultimate relationship.  Ideally the {@link isOneTrue} and
	 * {@link isTwoTrue} are properties of {@link one} and {@link two} 
	 * respectively without enforcing object inheritence.</p>
	 * <p>An exmaple implementation would look like:</p> 
	 * <p><code>biPermuation(objectA,
	 * objectB, objectA.booleanProperty, objectB.booleanProperty, consumerA,
	 * consumerB, consumerC)</code></p>
	 * <p>internally the {@link either} logic simply replaces which of the T
	 * parameters is passed first such that the first object in the consumer
	 * is the one with only true property
	 * 
	 * @param <T> any Object
	 * @param one an instance of T
	 * @param two an instance of T
	 * @param isOneTrue a property of one
	 * @param isTwoTrue a property of two
	 * @param either a BiConsumer for treating the first parameter as the only object with a true property
	 * @param neither a BiConsumer for if neither properties are true
	 * @param both a BiConsumer for if both properties are true
	 */
	public static <T> void biPermutation(T one, T two, boolean isOneTrue, boolean isTwoTrue, 
			BiConsumer<T,T> either,
			BiConsumer<T,T> neither,
			BiConsumer<T,T> both) {
		if (isOneTrue && !isTwoTrue) either.accept(one, two);
		else if (!isOneTrue && isTwoTrue) either.accept(two, one);
		else if (!isOneTrue && !isTwoTrue) neither.accept(one, two);
		else both.accept(one, two);
	}
}
