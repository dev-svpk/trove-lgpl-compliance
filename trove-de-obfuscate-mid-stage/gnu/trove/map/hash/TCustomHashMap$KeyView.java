package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import java.util.Iterator;

public class TCustomHashMap$KeyView extends TCustomHashMap$MapBackedView {
   // $FF: synthetic field
   final TCustomHashMap this$0;

   protected TCustomHashMap$KeyView(TCustomHashMap var1) {
      super(var1, (TCustomHashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new TObjectHashIterator(this.this$0);
   }

   public boolean removeElement(Object var1) {
      return this.this$0.remove(var1) != null;
   }

   public boolean containsElement(Object var1) {
      return this.this$0.contains(var1);
   }
}
