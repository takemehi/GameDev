package de.htw.saarland.gamedev.nap.data.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class CustomContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		//sensor test
		if(fA.getUserData()!=null && fA.getUserData().equals("sensor")){
			System.out.println("true");
		}
		if(fB.getUserData()!=null && fB.getUserData().equals("sensor")){
			System.out.println("true");
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		//contact.setEnabled(true);
		
		if(fA.getUserData()!=null && fA.getUserData().equals("sensor")){
			System.out.println("false");
		}
		if(fB.getUserData()!=null && fB.getUserData().equals("sensor")){
			System.out.println("false");
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//TODO calculate the offset instead of hardcoding it
		//TODO remove keypress argument
		//TODO find a way to delay dropping through the platform
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		//player jumping through a platform from below
		if(fA.getUserData()!=null && (fA.getUserData().equals("platformOne") || fA.getUserData().equals("platformTwo"))){
			if(fB.getBody().getPosition().y-2<fA.getBody().getPosition().y){
				contact.setEnabled(false);
			}
		}
		if(fB.getUserData()!=null && (fB.getUserData().equals("platformOne") || fB.getUserData().equals("platformTwo"))){
			if(fA.getBody().getPosition().y-2<fB.getBody().getPosition().y){
				contact.setEnabled(false);
				}
		}
		//player dropping through a platform from above
		if(fA.getUserData()!=null && fA.getUserData().equals("platformTwo")){
			if((Math.abs(fB.getBody().getPosition().y-2-fA.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S))
				contact.setEnabled(false);
		}
		if(fB.getUserData()!=null && fB.getUserData().equals("platformTwo")){
			if((Math.abs(fA.getBody().getPosition().y-2-fB.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S))
				contact.setEnabled(false);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
