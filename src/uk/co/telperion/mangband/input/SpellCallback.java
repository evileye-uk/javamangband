package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.ui.MangbandTerm;

public abstract class SpellCallback extends CharCallback {

	private boolean list_shown = false;
	private final int index;
	private final MangbandTerm term;

	public SpellCallback(CallbackUser client, MangbandTerm mainTerm, int index) {
		super(client);
		this.index = index;
		this.term = mainTerm;
	}

	@Override
	public void update(char ch) {
		if (ch >= 'a' && ch <= 'w') {
			if (list_shown) {
				term.restoreTerm();
			}

			updateSpell(ch);
			term.cancelInputMode();
		}
		if (ch == '*') {
			list_shown = true;
			client.show_book(index);
		} else if (ch == MangbandTerm.ESCAPE) {
			cancel();
		}
	}

	abstract public void updateSpell(char ch);

	private void cancel() {
		term.cancelInputMode();
		if (list_shown) {
			term.restoreTerm();
		}
		client.clearMessage();
	}
}
