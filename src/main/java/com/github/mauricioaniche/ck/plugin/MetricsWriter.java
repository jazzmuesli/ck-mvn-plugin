package com.github.mauricioaniche.ck.plugin;

import com.github.mauricioaniche.ck.CKNotifier;

public interface MetricsWriter extends CKNotifier {
	void init(String dirName);

	void finish();
}