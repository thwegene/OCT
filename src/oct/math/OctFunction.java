package oct.math;

/**
 * Minimal interface to allow the user to use custom functions.
 */
public interface OctFunction {

	/**
	 * To be implemented by the user, custom function.
	 */
	float compute(float x, float y, float z);

}
