/**
 *
 */
package com.alquimista.android.asktheoracle;

/**
 * @author rudy
 *
 */
public interface HttpProgressListener {

	public final static int REQUEST_CODE_GT = 0;
	public final static int REQUEST_CODE_WIKTIONARY = 1;
	public final static int REQUEST_CODE_WIKIPEDIA = 2;


	void onProgressLoading(int requestCode, String result);
}
