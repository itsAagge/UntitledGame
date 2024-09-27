package Client.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcurrentArrayList implements Iterable<ClientPlayer>
{
	private List<ClientPlayer> liste = new ArrayList<ClientPlayer>();
	
	public void add(ClientPlayer p) {
		liste.add(p);	
	}

	public void remove(ClientPlayer p) {
		liste.remove(p);	
	}
	
	public void clear() {
		liste.clear();
	}
	
	public int size() {
		return liste.size();
	}

	@Override
	public Iterator<ClientPlayer> iterator() {
		// TODO Auto-generated method stub
		return liste.iterator();
	}
	/*public class ConcurrentArrayListIterator<Player> implements Iterator<Player> {

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Player next() {
			// TODO Auto-generated method stub
			return null;
		}
	
}*/
}