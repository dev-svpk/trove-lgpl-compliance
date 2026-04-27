package gnu.trove.map;

public interface TFloatDoubleMap {
   double getNoEntryValue();

   double get(float var1);

   int size();

   gnu.trove.iterator.TFloatDoubleIterator iterator();
}
