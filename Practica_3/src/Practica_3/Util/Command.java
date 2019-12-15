package Practica_3.Util;

import com.sun.istack.internal.NotNull;
import com.sun.javafx.geom.Vec3d;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Bruno García Trípoli
 */
public enum Command implements IStringSerializable,IJsonSerializable{    
    CHECK_IN("check in","checkin"),
    RESCUE("rescue","rescue"),
    REFUEL("refuel","refuel"),
    STOP("stop","stop");
    
    private final String name;
    private final String jsonValue;
    private static final Command[] VALUES = values();
    private final static Map<String,IJsonSerializable> NAME_LOOKUP = new java.util.HashMap<String,IJsonSerializable>(){
        {
            Stream.concat(Arrays.stream(VALUES),Arrays.stream(Direction.VALUES)).forEach((item)->{
                put(item.getName(),item);
            });
        }
    };
    
    private final static Map<String,IJsonSerializable> JSON_LOOKUP = 
        new java.util.HashMap<String,IJsonSerializable>(){
            {
                Stream.concat(Arrays.stream(VALUES),Arrays.stream(Direction.VALUES)).forEach((item)->{
                    put(item.getJsonValue(),item);
                });
            }
        };        

    private Command(String name, String jsonValue){
        this.name=name;
        this.jsonValue = jsonValue;
    }   
    
    public static String[] getDisplayNames(){
        int length = NAME_LOOKUP.keySet().size();
        String[] arr = NAME_LOOKUP.keySet().toArray(new String[length]);
        Arrays.sort(arr);
        return arr;
    }
    
    public static IJsonSerializable[] getAllValues(){
        return JSON_LOOKUP.values().toArray(new IJsonSerializable[JSON_LOOKUP.values().size()]);
    }
    
    /**
     * Brb test works but Command.values() and Command.Direction.values() are better.
     * @param <T>
     * @param filter
     * @return 
     */
    public static <T extends IJsonSerializable & IStringSerializable> T[] 
        getAllValues(Class<T> filter)
    {  
        List<T> retList = new ArrayList<>();
        if(filter == Command.class)    
        {
            JSON_LOOKUP.values().forEach((item)->{
                if(item instanceof Command)
                    retList.add((T) item);
            });             
        }         
        else if(filter == Command.Direction.class)
        {
            JSON_LOOKUP.values().forEach((item)->{
                if(item instanceof Command.Direction)
                    retList.add((T) item);
            }); 
        }                 
        else
            throw new AssertionError("\"T must be Command or Command.Direction\"");
        
        T[] a = (T[]) Array.newInstance(filter, retList.size());
        for (int i = 0; i < retList.size(); i++) 
            a[i] = retList.get(i);        
        return a;
    }
    /**
    * Get the facing specified by the given name
    * @param name 
    * @return Command or Command.Direction as IJsonSerializable
    */        
    public static IJsonSerializable byName(String name) {
       return name == null ? null : NAME_LOOKUP.get(name);
    }
    
    /**
    * Get the facing specified by the given json value
    * @param name
    * @return Command or Command.Direction as IJsonSerializable
    */
    public static IJsonSerializable byJsonValue(String name) {
       return name == null ? null : NAME_LOOKUP.get(name);
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getJsonValue() {
        return jsonValue;
    }
    
    @Override
    public String toString() {
        return "Command{" + getName() + '}';
    }
    
    public enum Direction implements IStringSerializable,IJsonSerializable {
        DOWN(0, 1, -1, "down","moveDW", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3d(0, -1, 0)),
        UP(1, 0, -1, "up","moveUP", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3d(0, 1, 0)),
        NORTH(2, 3, 4, "north","moveN", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3d(0, 0, -1)),
        SOUTH(3, 2, 0, "south","moveS", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3d(0, 0, 1)),
        WEST(4, 5, 2, "west","moveW", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3d(-1, 0, 0)),
        EAST(5, 4, 6, "east","moveE", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3d(1, 0, 0)),
        NORTHWEST(6, 9, 3, "northwest","moveNW", null, null, new Vec3d(-1, 0, -1)),
        NORTHEAST(7, 8, 5, "northeast","moveNE", null, null, new Vec3d(1, 0, -1)),
        SOUTHWEST(8, 7, 1, "southwest","moveSW", null, null, new Vec3d(-1, 0, 1)),
        SOUTHEAST(9, 6, 7, "southeast","moveSE", null, null, new Vec3d(1, 0, 1));

        private final int index;
        private final int opposite;
        private final int horizontalIndex;
        private final String name;
        private final String jsonValue;
        private final Direction.Axis axis;
        private final Direction.AxisDirection axisDirection;
        private final Vec3d directionVec;
        private static final Direction[] VALUES = values();
        private static final Map<String, Direction> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName2, (p_199787_0_) -> {
           return p_199787_0_;
        }));
        private static final Direction[] BY_INDEX = Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
           return p_199790_0_.index;
        })).toArray((p_199788_0_) -> {
           return new Direction[p_199788_0_];
        });
        private static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES).filter((p_199786_0_) -> {           
            //return p_199786_0_.getAxis().isHorizontal();
            return p_199786_0_.horizontalIndex != -1; 
        }).sorted(Comparator.comparingInt((p_199789_0_) -> {
           return p_199789_0_.horizontalIndex;
        })).toArray((p_199791_0_) -> {
           return new Direction[p_199791_0_];
        });       

        private Direction(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn,String jsonValue, Direction.AxisDirection axisDirectionIn, Direction.Axis axisIn, Vec3d directionVecIn) {
           this.index = indexIn;
           this.horizontalIndex = horizontalIndexIn;
           this.opposite = oppositeIn;
           this.name = nameIn;
           this.jsonValue = jsonValue;
           this.axis = axisIn;
           this.axisDirection = axisDirectionIn;
           this.directionVec = directionVecIn;
        }       

        /**         
         * @return The Index of this Facing (0-9). The order is D-U-N-S-W-E-NW-NE-SW-SE
         */
        public int getIndex() {
           return this.index;
        }

        /**
         * @return The Index of this Facing (0-7). The order is S-SW-W-NW-N-NE-E-SE                
         */
        public int getHorizontalIndex() {
           return this.horizontalIndex;
        }

        /**
         * @return The AxisDirection of this Facing.
         */
        public Direction.AxisDirection getAxisDirection() {
           return this.axisDirection;
        }

        /**
         * @return The opposite Facing (e.g. DOWN => UP)
         */
        public Direction getOpposite() {
           return byIndex(this.opposite);
        }

        /**
         * @param axis The axis used to apply the rotation.
         * @return
         * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
         * returns this facing without rotating.
         */
        public Direction rotateAround(Direction.Axis axis) {
           switch(axis) {
           case X:
              if (this != WEST && this != EAST) {
                 return this.rotateX();
              }

              return this;
           case Y:
              if (this != UP && this != DOWN) {
                 return this.rotateY();
              }

              return this;
           case Z:
              if (this != NORTH && this != SOUTH) {
                 return this.rotateZ();
              }

              return this;
           default:
              throw new IllegalStateException("Unable to get CW facing for axis " + axis);
           }
        }

        /**
         * Rotate this Facing around the Y axis clockwise.         *  
         * (NORTH => NORTHEAST => EAST => SOUTHEAST => SOUTH => SOUTHWEST => WEST => NORTHEAST => NORTH)
         * Note: UP and DOWN doesn't suffer any change.
         * @return Direction         
         */
        public Direction rotateY() {
           if(!Direction.Plane.HORIZONTAL.test(this))
               return this;
           int lenght = Direction.BY_HORIZONTAL_INDEX.length;
           return Direction.byHorizontalIndex(this.horizontalIndex+1);
        }

        /**
         * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
         */
        private Direction rotateX() {
           switch(this) {
           case NORTH:
              return DOWN;   
           case SOUTH:
              return UP;
           case UP:
              return NORTH;
           case DOWN:
              return SOUTH;
            default:
              throw new IllegalStateException("Unable to get X-rotated facing of " + this);
           }
        }

        /**
         * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
         */
        private Direction rotateZ() {
           switch(this) {
           case EAST:
              return DOWN;                     
           case WEST:
              return UP;
           case UP:
              return EAST;
           case DOWN:
              return WEST;
            default:
              throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
           }
        }

        /**
         * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
         * @return A Direction
         */
        public Direction rotateYCCW() {
          if(!Direction.Plane.HORIZONTAL.test(this))
               return this;
           int length = Direction.BY_HORIZONTAL_INDEX.length;
           return Direction.byHorizontalIndex(this.horizontalIndex-1);
        }

        /**
         * @return The offset in the x direction to the block in front of this facing.
         */
        public int getXOffset() {
           return this.axis == Direction.Axis.X ? this.axisDirection.getOffset() : 0;
        }

        /**
         * @return The offset in the y direction to the block in front of this facing.
         */
        public int getYOffset() {
           return this.axis == Direction.Axis.Y ? this.axisDirection.getOffset() : 0;
        }

        /**
         * @return The offset in the z direction to the block in front of this facing.
         */
        public int getZOffset() {
           return this.axis == Direction.Axis.Z ? this.axisDirection.getOffset() : 0;
        }

        /**
         * Same as getName, but does not override the method from Enum.
         */
        private String getName2() {
           return this.name;
        }

        public Direction.Axis getAxis() {
           return this.axis;
        }

        /**
         * @param name The display name of the Direction
         * @return The facing specified by the given name
         */
        public static Direction byName(String name) {
           return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
        }

        /**
         * The order is D-U-N-S-W-E-NW-NE-SW-SE.
         * @param index Out of bounds (0-9) values are wrapped around.
         * @return The EnumFacing corresponding to the given index. 
         */
        public static Direction byIndex(int index) {
           int lenght = Direction.BY_INDEX.length;
           if(index<0){
                index+=lenght;
                return Direction.BY_INDEX[index];
           }
           else
               return Direction.BY_INDEX[index%lenght];
        }

        /**
         * Gets the EnumFacing corresponding to the given horizontal index (0-3). Out of bounds values are wrapped around.
         * The order is S-W-N-E.
         */
        /**
         * The order is S-SW-W-NW-N-NE-E-SE.
         * @param horizontalIndexIn Out of bounds (0-7) values are wrapped around.
         * @return The EnumFacing corresponding to the given index. 
         */
        public static Direction byHorizontalIndex(int horizontalIndexIn) {
           int lenght = Direction.BY_HORIZONTAL_INDEX.length;
           if(horizontalIndexIn<0){
                horizontalIndexIn+=lenght;
                return Direction.BY_HORIZONTAL_INDEX[horizontalIndexIn];
           }
           else
               return Direction.BY_HORIZONTAL_INDEX[horizontalIndexIn%lenght];
        }

        /**
         * @param angle given angle in degrees (0-360). Out of bounds values are wrapped around.
         * @return The EnumFacing corresponding to the given angle
         * An angle of 0 is NORTH, an angle of 90 would be EAST.
         */
        public static Direction fromAngle(double angle) {
           double clamp_angle = angle;
           if(clamp_angle>=360) clamp_angle=clamp_angle%360;
           while(clamp_angle<0) clamp_angle+=360;
           
           if(clamp_angle >= 22.5 && clamp_angle < 67.5) //NE
               return Direction.NORTHEAST;
           else if(clamp_angle >= 67.5 && clamp_angle < 112.5) //E
               return Direction.EAST;
           else if(clamp_angle >= 112.5 && clamp_angle < 157.5) //SE
               return Direction.SOUTHEAST;
           else if(clamp_angle >= 157.5 && clamp_angle < 202.5) //S
               return Direction.SOUTH;
           else if(clamp_angle >= 202.5 && clamp_angle < 247.5) //SW
               return Direction.SOUTHWEST;
           else if(clamp_angle >= 247.5 && clamp_angle < 292.5) //W
               return Direction.WEST;
           else if(clamp_angle >= 292.5 && clamp_angle < 337.5) //NW
               return Direction.NORTHWEST;
           else //N
               return Direction.NORTH;                  
        }

        public static Direction getFacingFromAxisDirection(Direction.Axis axisIn, Direction.AxisDirection axisDirectionIn) {
           switch(axisIn) {
           case X:
              return axisDirectionIn == Direction.AxisDirection.POSITIVE ? EAST : WEST;
           case Y:
              return axisDirectionIn == Direction.AxisDirection.POSITIVE ? UP : DOWN;
           case Z:
           default:
              return axisDirectionIn == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
           }
        }

        /**
         * @return The angle in degrees corresponding to this EnumFacing.
         */
        public float getHorizontalAngle() {
           return (float)((this.horizontalIndex & 3) * 90);
        }

        /**
         * @param rand Random Seed
         * @return A random Direction using the given Random
         */
        public static Direction random(Random rand) {
           return values()[rand.nextInt(values().length)];
        }

        public static Direction getFacingFromVector(double x, double y, double z) {
           return getFacingFromVector((float)x, (float)y, (float)z);
        }

        public static Direction getFacingFromVector(float x, float y, float z) {
           Direction direction = NORTH;
           float f = Float.MIN_VALUE;

           for(Direction direction1 : VALUES) {
              float f1 = x * (float)direction1.directionVec.x + y * (float)direction1.directionVec.y + z * (float)direction1.directionVec.z;
              if (f1 > f) {
                 f = f1;
                 direction = direction1;
              }
           }

           return direction;
        }

        @Override
        public String toString() {
           return "Direction{" + getName() + '}';
        }

        /**
         * 
         * @return The name
         */
        @Override
        public String getName() {
           return this.name;
        }

        public static Direction getFacingFromAxis(Direction.AxisDirection axisDirectionIn, Direction.Axis axisIn) {
           for(Direction direction : values()) {
              if (direction.getAxisDirection() == axisDirectionIn && direction.getAxis() == axisIn) {
                 return direction;
              }
           }

           throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
        }

        /**
         * @return a NOT normalized Vector that points in the direction of this Facing.
         */
        public Vec3d getDirectionVec() {
           return this.directionVec;
        }

        
        /**         
         * @return The json value of Enum value.
         */
        @Override
        public String getJsonValue() {
            return jsonValue;
        }

        public static enum Axis implements IStringSerializable, Predicate<Direction> {
           X("x") {
              @Override
              public int getCoordinate(int x, int y, int z) {
                 return x;
              }

              @Override
              public double getCoordinate(double x, double y, double z) {
                 return x;
              }
           },
           Y("y") {
              @Override
              public int getCoordinate(int x, int y, int z) {
                 return y;
              }

              @Override
              public double getCoordinate(double x, double y, double z) {
                 return y;
              }
           },
           Z("z") {
              @Override
              public int getCoordinate(int x, int y, int z) {
                 return z;
              }

              @Override
              public double getCoordinate(double x, double y, double z) {
                 return z;
              }
           };

           private static final Map<String, Direction.Axis> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(Direction.Axis::getName2, (p_199785_0_) -> {
              return p_199785_0_;
           }));
           private final String name;

           private Axis(String nameIn) {
              this.name = nameIn;
           }

           /**
             * @param name name
            * @return The axis specified by the given name
            */
           public static Direction.Axis byName(String name) {
              return NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
           }

           /**
            * Like getName but doesn't override the method from Enum.
            */
           private String getName2() {
              return this.name;
           }

           /**            
            * @return If this Axis is on the vertical plane (true for Y)
            */
           public boolean isVertical() {
              return this == Y;
           }

           /**
            * @return If this Axis is on the horizontal plane (true for X and Z)
            */
           public boolean isHorizontal() {
              return this == X || this == Z;
           }
          
           @Override
           public String toString() {
              return this.name;
           }

           /**
           * @param p_218393_0_ Random Seed
           * @return A random Direction.Axis using the given Random
           */
           public static Direction.Axis random(Random p_218393_0_) {
              return values()[p_218393_0_.nextInt(values().length)];
           }

           /**            
            * @param p_test_1_ Direction to check
            * @return If this Axis has the direction return true if not false
            */
           @Override
           public boolean test(Direction p_test_1_) {
              return p_test_1_ != null && p_test_1_.getAxis() == this;
           }

           /**
            * @return This Axis' Plane (VERTICAL for Y, HORIZONTAL for X and Z)
            */
           public Direction.Plane getPlane() {
              switch(this) {
              case X:
              case Z:
                 return Direction.Plane.HORIZONTAL;
              case Y:
                 return Direction.Plane.VERTICAL;
              default:
                 throw new Error("Someone's been tampering with the universe!");
              }
           }

           /**
            * 
            * @return name
            */
           @Override
           public String getName() {
              return this.name;
           }

           
           public abstract int getCoordinate(int x, int y, int z);

           public abstract double getCoordinate(double x, double y, double z);
        }

        public static enum AxisDirection {
           POSITIVE(1, "Towards positive"),
           NEGATIVE(-1, "Towards negative");

           private final int offset;
           private final String description;

           private AxisDirection(int offset, String description) {
              this.offset = offset;
              this.description = description;
           }

           /**
            * @return The offset for this AxisDirection.1 for POSITIVE, -1 for NEGATIVE
            */
           public int getOffset() {
              return this.offset;
           }

           @Override
           public String toString() {
              return this.description;
           }
        }

        public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
           HORIZONTAL(new Direction[]{Direction.NORTH,Direction.NORTHEAST, 
               Direction.EAST,Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST ,
               Direction.WEST, Direction.NORTHWEST}, 
               new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
           VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

           private final Direction[] facingValues;
           private final Direction.Axis[] axisValues;

           private Plane(Direction[] facingValuesIn, Direction.Axis[] axisValuesIn) {
              this.facingValues = facingValuesIn;
              this.axisValues = axisValuesIn;
           }

           /**
            * @param rand Random seed
            * @return A random Facing from this Plane using the given Random
            */
           public Direction random(Random rand) {
              return this.facingValues[rand.nextInt(this.facingValues.length)];
           }

           @Override
           public boolean test(Direction p_test_1_) {
               if(p_test_1_==null)
                   return false;               
               if(p_test_1_.getHorizontalIndex()!=-1 && this == Plane.HORIZONTAL)
                   return true;
               //return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
               return p_test_1_.getHorizontalIndex()==-1 && this == Plane.VERTICAL;
           }

           @Override
           public Iterator<Direction> iterator() {
              return Arrays.asList(facingValues).iterator();
           }
        }
        
        public static Direction parse(@NotNull String name){
            return NAME_LOOKUP.get(name);
        }
     }
    
}
