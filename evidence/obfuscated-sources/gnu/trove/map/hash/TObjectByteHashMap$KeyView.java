package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.ca.a;
import java.util.Iterator;

public class TObjectByteHashMap$KeyView extends TObjectByteHashMap$MapBackedView {
   // $FF: synthetic field
   final TObjectByteHashMap this$0;

   protected TObjectByteHashMap$KeyView(TObjectByteHashMap var1) {
      super(var1, (TObjectByteHashMap$MapBackedView)null);
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
