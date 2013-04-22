package canon;

public class TraceSchedule {

  public tree.StmList stms;
  BasicBlocks theBlocks;
  java.util.Dictionary table = new java.util.Hashtable();

  tree.StmList getLast(tree.StmList block) {
     tree.StmList l=block;
     while (l.tail.tail!=null)  l=l.tail;
     return l;
  }

  void trace(tree.StmList l) {
   for(;;) {
     tree.LABEL lab = (tree.LABEL)l.head;
     table.remove(lab.label);
     tree.StmList last = getLast(l);
     tree.Stm s = last.tail.head;
     if (s instanceof tree.JUMP) {
	tree.JUMP j = (tree.JUMP)s;
        tree.StmList target = (tree.StmList)table.get(j.targets.head);
	if (j.targets.tail==null && target!=null) {
               last.tail=target;
	       l=target;
        }
	else {
	  last.tail.tail=getNext();
	  return;
        }
     }
     else if (s instanceof tree.CJUMP) {
	tree.CJUMP j = (tree.CJUMP)s;
        tree.StmList t = (tree.StmList)table.get(j.iftrue);
        tree.StmList f = (tree.StmList)table.get(j.iffalse);
        if (f!=null) {
	  last.tail.tail=f; 
	  l=f;
	}
        else if (t!=null) {
	  last.tail.head=new tree.CJUMP(tree.CJUMP.notRel(j.relop),
					j.left,j.right,
					j.iffalse,j.iftrue);
	  last.tail.tail=t;
	  l=t;
        }
        else {
	  temp.Label ff = new temp.Label();
	  last.tail.head=new tree.CJUMP(j.relop,j.left,j.right,
					j.iftrue,ff);
	  last.tail.tail=new tree.StmList(new tree.LABEL(ff),
		           new tree.StmList(new tree.JUMP(j.iffalse),
					    getNext()));
	  return;
        }
     }
     else throw new Error("Bad basic block in TraceSchedule");
    }
  }

  tree.StmList getNext() {
      if (theBlocks.blocks==null) 
	return new tree.StmList(new tree.LABEL(theBlocks.done), null);
      else {
	 tree.StmList s = theBlocks.blocks.head;
	 tree.LABEL lab = (tree.LABEL)s.head;
	 if (table.get(lab.label) != null) {
          trace(s);
	  return s;
         }
         else {
	   theBlocks.blocks = theBlocks.blocks.tail;
           return getNext();
         }
      }
  }

  public TraceSchedule(BasicBlocks b) {
    theBlocks=b;
    for(StmListList l = b.blocks; l!=null; l=l.tail)
       table.put(((tree.LABEL)l.head.head).label, l.head);
    stms=getNext();
    table=null;
  }        
}
