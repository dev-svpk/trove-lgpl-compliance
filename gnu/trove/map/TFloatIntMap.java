package gnu.trove.map;

public interface TFloatIntMap {
   int getNoEntryValue();

   int get(float var1);

   int size();

   gnu.trove.iterator.TFloatIntIterator iterator();
}
