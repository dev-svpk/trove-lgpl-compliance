package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.ca.a;
import java.util.Iterator;

public class THashMap$KeyView extends THashMap$MapBackedView {
   // $FF: synthetic field
   final THashMap this$0;

   protected THashMap$KeyView(THashMap var1) {
      super(var1, (THashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new a(this.this$0);
   }

   public boolean removeElement(Object var1) {
      return this.this$0.remove(var1) != null;
   }

   public boolean containsElement(Object var1) {
      return this.this$0.contains(var1);
   }
}
