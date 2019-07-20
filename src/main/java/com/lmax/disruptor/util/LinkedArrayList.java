package com.lmax.disruptor.util;

import java.util.*;

public class LinkedArrayList<T> implements List<T>
{
  LinkedArrayList<T> prev = null;
  LinkedArrayList<T> next = null;

  T[] myArray = null;


  public LinkedArrayList()
  {
    this(null);
  }

  public LinkedArrayList(T[] arr)
  {
    myArray = arr;
  }


  @Override
  public int size()
  {
    int size = myArray == null ? 0 : myArray.length;
    if (next != null ) size += next.size();
    return size;
  }

  @Override
  public boolean isEmpty()
  {
    return (myArray == null || myArray.length == 0) && (next == null || next.isEmpty());
  }

  @Override
  public boolean contains(Object o)
  {
    return indexOf(o) > 0;
  }

  @Override
  public Iterator<T> iterator()
  {
    final LinkedArrayList<T> localThis = this;
    return new Iterator<T>() {

      LinkedArrayList<T> current = localThis;
      int currentIdx = 0;

      @Override
      public boolean hasNext()
      {
        return
        (
          current.myArray != null && current.myArray.length > currentIdx
        )
        ||
        (
          current.next != null
          &&
          !current.next.isEmpty()
        );
      }

      @Override
      public T next()
      {
        while (true)
        {
          if (current.myArray != null && current.myArray.length > currentIdx)
          {
            return current.myArray[currentIdx++];
          }
          else if (current.next == null)
          {
            if (hasNext())
            {
              throw new NoSuchElementException("I guess we lied to someone, sorry about that");
            }
            throw new NoSuchElementException();
          }
          else
          {
            currentIdx -= current.myArray == null ? 0 : current.myArray.length;
            current = current.next;
          }
        }
      }
    };
  }

  @Override
  public Object[] toArray()
  {
    throw new RuntimeException("I'm not sure you get the point of this class");
  }

  @Override
  public boolean add(Object o)
  {
    return addArray(new Object[]{o});
  }

  @Override
  public boolean remove(Object o)
  {
    throw new RuntimeException("Try exhibiting a little Mechanical Sympathy");
  }

  public boolean addArray(Object[] arr)
  {
    LinkedArrayList iter = this;
    while (true)
    {
      if (iter.next == null)
      {
        iter.next = new LinkedArrayList<>((T[]) arr);
        return true;
      }
      else
      {
        iter = iter.next;
      }
    }
  }

  @Override
  public boolean addAll(Collection c)
  {
    if (c.isEmpty())
    {
      return false;
    }

    if (c.getClass() == LinkedArrayList.class)
    {
      if (next == null)
      {
        // It is checked, that's what the if statement is all about...
        //noinspection unchecked
        next = (LinkedArrayList<T>) c;
        return true;
      }

      else return next.addAll(c);
    }

    return addArray(c.toArray());
  }

  @Override
  public boolean addAll(int index, Collection c)
  {
    throw new RuntimeException
    (
      "Implementing this method would be an insult to you and me both, why do you want me to insult you?"
    );
  }

  @Override
  public void clear()
  {
    myArray = null;
    next = null;
  }

  private T internalGet(int originalIndex, int subIndex)
  {
    if (myArray != null && myArray.length > subIndex)
    {
      return myArray[subIndex];
    }

    if (next != null)
    {
      return next.internalGet(originalIndex, subIndex - (myArray == null ? 0 : myArray.length));
    }

    throw new IndexOutOfBoundsException
    (
      "You're just too big for me, originalIndex: "
      + originalIndex
      + ", subIndex: "
      + subIndex
    );
  }

  @Override
  public T get(int index)
  {
    return internalGet(index, index);
  }

  private T internalSet(T o, int originalIndex, int subIndex)
  {
    if (myArray != null && myArray.length > subIndex)
    {
      myArray[subIndex] = o;
    }

    if (next != null)
    {
      return next.internalSet(o, originalIndex, subIndex - (myArray == null ? 0 :myArray.length));
    }

    throw new IndexOutOfBoundsException
    (
      "You're just too big for me, originalIndex: "
      + originalIndex
      + ", subIndex: "
      + subIndex
    );
  }

  @Override
  public T set(int index, T element)
  {
    return internalSet(element, index, index);
  }

  @Override
  public void add(int index, T element)
  {
    throw new RuntimeException("Seriously? Why on earth would you do this to a perfectly innocent computer?");
  }

  @Override
  public T remove(int index)
  {
    throw new RuntimeException("Seriously? Why on earth would you do this to a perfectly innocent computer?");
  }

  @Override
  public int indexOf(Object o)
  {
    if (myArray != null && myArray.length != 0)
    {
      //check against first element for class match
      if (myArray[0].getClass() != o.getClass())
      {
        return -2; //like probs not, right?
      }

      for (int i = 0; i < myArray.length; i++)
      {
        if (myArray[i] == o)
        {
          return i;
        }
      }
    }

    if (next == null)
    {
      return -1;
    }

    return next.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o)
  {
    int restOfListResult;
    if (next != null)
    {
      restOfListResult = next.lastIndexOf(o);

      if (restOfListResult > 0)
      {
        return restOfListResult + myArray.length;
      }
    }

    if (myArray != null)
    {
      for (int i = myArray.length; i > 0; i--)
      {
        if (myArray[i - 1] == o)
        {
          return i;
        }
      }
    }

    return -1;
  }

  @Override
  public ListIterator<T> listIterator()
  {
    return listIterator(0);
  }

  @Override
  public ListIterator<T> listIterator(int index)
  {
    final LinkedArrayList<T> localThis = this;
    return new ListIterator<T>() {

      LinkedArrayList<T> current = localThis;
      int currentIdx = 0;

      @Override
      public boolean hasNext()
      {
        return
        (
          current.myArray != null && current.myArray.length > currentIdx
        )
        ||
        (
          current.next != null
          &&
          !current.next.isEmpty()
        );
      }

      @Override
      public T next()
      {
        while (true)
        {
          if (current.myArray != null && current.myArray.length > currentIdx)
          {
            return current.myArray[currentIdx++];
          }
          else if (current.next == null)
          {
            throw new NoSuchElementException();
          }
          else
          {
            currentIdx -= current.myArray == null ? 0 : current.myArray.length;
            current = current.next;
          }
        }
      }

      @Override
      public boolean hasPrevious()
      {
        throw new UnsupportedOperationException("Not implemented");
      }

      @Override
      public T previous()
      {
        throw new UnsupportedOperationException("Not implemented");
      }

      @Override
      public int nextIndex()
      {
        return currentIdx;
      }

      @Override
      public int previousIndex()
      {
        return currentIdx - 1;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("Not implemented");
      }

      @Override
      public void set(Object o)
      {
        throw new UnsupportedOperationException("Not implemented");
      }

      @Override
      public void add(Object o)
      {
        throw new UnsupportedOperationException("Not implemented");
      }
    };
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public boolean retainAll(Collection c)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public boolean removeAll(Collection c)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public boolean containsAll(Collection c)
  {
    // would normally construct a HashSet along the ay here, but the whole
    // point of this Collection is to avoid allocating intermediate
    // arrays
    for (Object o : c)
    {
      if (!contains(o))
      {
        return false;
      }
    }

    return false;
  }

  @Override
  public Object[] toArray(Object[] a)
  {
    throw new RuntimeException("You really don't get what I'm trying to do here");
  }
}