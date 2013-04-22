package frame;

public abstract class Access {
  // Given a tree.Exp that computes the base pointer, an Access can
  // produce a tree.Exp that accesses the variable.
  // 
  // Notice that we are generalizing the specification on pages 143-144,
  // because in MiniJava there are *three* places where a variable can
  // reside: in a register, in a stack frame, and in an object.
  // For a frame-resident variable, the base pointer is the frame pointer.
  // For a field in an object, the base pointer is the "this" pointer
  // to the object.

  abstract public tree.Exp exp(tree.Exp basePtr);
}
