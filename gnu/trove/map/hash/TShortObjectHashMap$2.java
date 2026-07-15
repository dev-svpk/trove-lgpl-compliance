package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TShortObjectProcedure;

class TShortObjectHashMap$2 implements TShortObjectProcedure {
   private boolean first;
   // $FF: synthetic field
   final TShortObjectHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TShortObjectHashMap$2(TShortObjectHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(short var1, Object var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
