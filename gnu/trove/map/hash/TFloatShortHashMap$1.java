package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TFloatShortProcedure;

class TFloatShortHashMap$1 implements TFloatShortProcedure {
   private boolean first;
   // $FF: synthetic field
   final TFloatShortHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TFloatShortHashMap$1(TFloatShortHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1, short var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
