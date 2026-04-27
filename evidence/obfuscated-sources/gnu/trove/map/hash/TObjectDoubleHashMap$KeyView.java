package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.ca.a;
import java.util.Iterator;

public class TObjectDoubleHashMap$KeyView extends TObjectDoubleHashMap$MapBackedView {
   // $FF: synthetic field
   final TObjectDoubleHashMap this$0;

   protected TObjectDoubleHashMap$KeyView(TObjectDoubleHashMap var1) {
      super(var1, (TObjectDoubleHashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new a(this.this$0);
   }

   public boolean removeElement(Object var1) {
      return this.this$0.no_entry_value != this.this$0.remove(var1);
   }

   public boolean containsElement(Object var1) {
      return this.this$0.contains(var1);
   }
}
