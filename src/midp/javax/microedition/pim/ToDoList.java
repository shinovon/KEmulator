package javax.microedition.pim;

import java.util.Enumeration;

public abstract interface ToDoList
        extends PIMList {
    public abstract ToDo createToDo();

    public abstract ToDo importToDo(ToDo paramToDo);

    public abstract void removeToDo(ToDo paramToDo)
            throws PIMException;

    public abstract Enumeration items(int paramInt, long paramLong1, long paramLong2)
            throws PIMException;
}
