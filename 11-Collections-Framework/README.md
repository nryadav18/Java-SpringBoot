# Topic 11 - Collections Framework

Java's ready-made data structures: List, Set, Map, Queue/Deque - which one to pick, how to loop and
remove safely, how to sort (Comparable vs Comparator), immutable collections, and how HashMap works inside.
Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_WhyCollections.java` | Array pain vs ArrayList; autoboxing |
| `Example02_ListArrayListVsLinkedList.java` | List methods, remove(index) vs remove(object), ArrayList vs LinkedList speed demo |
| `Example03_SetTypes.java` | HashSet / LinkedHashSet / TreeSet, removing duplicates, union/intersection/difference |
| `Example04_MapTypes.java` | Map methods, looping with entrySet, HashMap / LinkedHashMap / TreeMap, counting |
| `Example05_QueueAndDeque.java` | Queue (FIFO), stack (LIFO) with ArrayDeque, PriorityQueue |
| `Example06_IteratorAndConcurrentModification.java` | Iterator, ConcurrentModificationException and 3 safe ways to remove |
| `Example07_ComparableVsComparator.java` | Natural order vs custom orders, multi-level sorting |
| `Example08_CollectionsUtilityAndImmutable.java` | `Collections` helpers, `List.of`/`Map.of`, view vs copy, `Arrays.asList` |
| `Example09_HowHashMapWorks.java` | hashCode, buckets, collisions, why equals + hashCode must go together |

## How to run (terminal opened in this folder)
```
java Example01_WhyCollections.java
java Example09_HowHashMapWorks.java
```
