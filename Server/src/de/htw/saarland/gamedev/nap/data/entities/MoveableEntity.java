package de.htw.saarland.gamedev.nap.data.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;

/**
 * Moveable entities represent things like players or npcs.
 * 
 * @author Stefan
 *
 */
public class MoveableEntity extends Entity{
	
	private static final String EXCEPTION_NULL_VELOCITY_BASE = "MaxVelocity object is null!";
	private static final String EXCEPTION_NULL_VELOCITY_MAX = "MaxVelocity object is null!";
	
	private Vector2 baseVelocity;
	private Vector2 maxVelocity;
	
	public MoveableEntity(World world, Shape shape, float density,
			float friction, float restitution, Vector2 position, Vector2 baseVelocity, Vector2 maxVelocity, int id) {
		super(world, shape, position, id);
		if(baseVelocity==null) throw new NullPointerException(EXCEPTION_NULL_VELOCITY_BASE);
		if(maxVelocity==null) throw new NullPointerException(EXCEPTION_NULL_VELOCITY_MAX);
		this.baseVelocity=baseVelocity;
		this.maxVelocity=maxVelocity;
		
		getBodyDef().type=BodyDef.BodyType.DynamicBody;
		getBodyDef().bullet=true;
		getBodyDef().fixedRotation=true;
		getFixtureDef().density=density;
		getFixtureDef().friction=friction;
		getFixtureDef().restitution=restitution;
	}

	public MoveableEntity(World world, Shape shape, float density,
			float friction, float restitution, float xPos, float yPos, float xVelBase, float yVelBase, int id){
		this(world, shape, density, friction, restitution, new Vector2(xPos,yPos), new Vector2(xVelBase,yVelBase), new Vector2(xVelBase, yVelBase), id);
	}
	
	public Vector2 getBaseVelocity(){
		return baseVelocity;
	}
	
	public Vector2 getMaxVelocity(){
		return maxVelocity;
	}
	
	// TODO definitely an error in this method returns sometimes false when the char is moving and grounded
	public boolean isGrounded(){
		if(isInitialized()){
			if(getBody().getLinearVelocity().y<=0){
				//calculate y offset
				Vector2 tmpVector = new Vector2();
				PolygonShape tmpShape= (PolygonShape) getFixture().getShape();
				tmpShape.getVertex(0, tmpVector);
				
				for(Contact c: getWorld().getContactList()){
					if(c.isTouching()
							&& (c.getFixtureA()==getFixture()
							|| c.getFixtureB()==getFixture())){
						Vector2 pos = getBody().getPosition();
						WorldManifold manifold = c.getWorldManifold();
						boolean below = false;
						if(Math.abs(c.getWorldManifold().getNormal().x)<Math.abs(c.getWorldManifold().getNormal().y)){
							below=true;
							for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
								below &= (manifold.getPoints()[j].y < pos.y - Math.abs(tmpVector.y));
							}
							if((c.getFixtureA()==getFixture() 
									&& c.getFixtureB().getUserData()!=null && c.getFixtureB().getUserData().equals(PlayableCharacter.USERDATA_PLAYER)))
								below=false;
							if((c.getFixtureB()==getFixture() 
									&& c.getFixtureA().getUserData()!=null && c.getFixtureA().getUserData().equals(PlayableCharacter.USERDATA_PLAYER)))
								below=false;
							if(below) return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}
