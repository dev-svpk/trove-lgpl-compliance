package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import java.util.Iterator;

public class TObjectLongHashMap$KeyView extends TObjectLongHashMap$MapBackedView {
   // $FF: synthetic field
   final TObjectLongHashMap this$0;

   protected TObjectLongHashMap$KeyView(TObjectLongHashMap var1) {
      super(var1, (TObjectLongHashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new TObjectHashIterator(this.this$0);
   }

   public boolean removeElement(Object var1) {
      return this.this$0.no_entry_value != this.this$0.remove(var1);
   }

   public boolean containsElement(Object var1) {
      return this.this$0.contains(var1);
   }
}
