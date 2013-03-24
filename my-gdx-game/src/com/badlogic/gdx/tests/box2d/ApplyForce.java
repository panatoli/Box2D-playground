/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.badlogic.gdx.tests.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.TimeUtils;

public class ApplyForce extends Box2DTest {
	Body m_body;
	Body bodies[][];
	private static int n = 3;
	private Texture jelly;
	private int frames;
	@Override
	protected void createWorld (World world) {
		world.setGravity(new Vector2(0, -100));
		jelly = new Texture("data/jelly_green.png");
		float k_restitution = 0.0f;
		Body ground;

		{
			BodyDef bd = new BodyDef();
			bd.position.set(0, 20);
			ground = world.createBody(bd);

			EdgeShape shape = new EdgeShape();

			FixtureDef sd = new FixtureDef();
			sd.shape = shape;
			sd.density = 0;
			sd.restitution = k_restitution;

			shape.set(new Vector2(-20, -20), new Vector2(-20, 20));
			ground.createFixture(sd);

			shape.set(new Vector2(20, -20), new Vector2(20, 20));
			ground.createFixture(sd);

			shape.set(new Vector2(-20, 20), new Vector2(20, 20));
			ground.createFixture(sd);

			shape.set(new Vector2(-20, -20), new Vector2(20, -20));
			ground.createFixture(sd);

			shape.dispose();
		}

		if (false) {
			Transform xf1 = new Transform(new Vector2(), 0.3524f * (float)Math.PI);
			xf1.setPosition(xf1.mul(new Vector2(1, 0)));

			Vector2[] vertices = new Vector2[3];
			vertices[0] = xf1.mul(new Vector2(-1, 0));
			vertices[1] = xf1.mul(new Vector2(1, 0));
			vertices[2] = xf1.mul(new Vector2(0, 0.5f));

			PolygonShape poly1 = new PolygonShape();
			poly1.set(vertices);

			FixtureDef sd1 = new FixtureDef();
			sd1.shape = poly1;
			sd1.density = 4.0f;

			Transform xf2 = new Transform(new Vector2(), -0.3524f * (float)Math.PI);
			xf2.setPosition(xf2.mul(new Vector2(-1, 0)));

			vertices[0] = xf2.mul(new Vector2(-1, 0));
			vertices[1] = xf2.mul(new Vector2(1, 0));
			vertices[2] = xf2.mul(new Vector2(0, 0.5f));

			PolygonShape poly2 = new PolygonShape();
			poly2.set(vertices);

			FixtureDef sd2 = new FixtureDef();
			sd2.shape = poly2;
			sd2.density = 2.0f;

			BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.angularDamping = 5.0f;
			bd.linearDamping = 0.1f;

			bd.position.set(0, 2);
			bd.angle = (float)Math.PI;
			bd.allowSleep = false;
			m_body = world.createBody(bd);
			m_body.createFixture(sd1);
			m_body.createFixture(sd2);
			poly1.dispose();
			poly2.dispose();
		}

		{
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(0.5f, 0.5f);

			FixtureDef fd = new FixtureDef();

			// create n x n mesh

			bodies = new Body[n][n];
			
			shape = new PolygonShape();
			shape.setAsBox(0.1f, 0.1f);
			
			
			fd = new FixtureDef();
			fd.shape = shape;
			fd.density = 10.0f;
			fd.friction = 0.0f;
			fd.restitution = 0.0f;
			
			float length = 8f;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					BodyDef bd = new BodyDef();
					bd.type = BodyType.DynamicBody;
					bd.position.set(0.2f + (float) i * length/ (float) (n - 1), (float) j * length/ (float) (n - 1) + 4.2f);
					Body body = world.createBody(bd);
					body.createFixture(fd);
					bodies[i][j]= body;
				}
			}
			
			float delta = length / (float) (n - 1);
			//Gdx.app.debug("", "delta = " + delta);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					Body body = bodies[i][j];
					Body body2;
					if (i < n - 1) {
						body2 = bodies[i+1][j];
						join(body, body2, delta);
					}
					
					if (j < n - 1) {
						body2 = bodies[i][j+1];
						join(body, body2, delta);
					}
					
					if (i < n - 1 && j < n - 1) {
						body2 = bodies[i + 1][j + 1];
						join(body, body2, delta * (float)Math.sqrt(2));
					}
					
					if (i < n - 1 && j > 0) {
						body2 = bodies[i + 1][j - 1];
						join(body, body2, delta * (float)Math.sqrt(2f));
					}					
				}
			}
			shape.dispose();
		}
	}

	private void join(Body b1, Body b2, float length) {
		DistanceJointDef jd = new DistanceJointDef(); 
		jd.localAnchorA.set(0, 0);
		jd.localAnchorB.set(0, 0);
		jd.bodyA = b1;
		jd.bodyB = b2;
		jd.collideConnected = true;
		//jd.maxLength = 10 + 2 * i;
		jd.length = length;
		//jd.maxForce = mass * gravity;
		//jd.maxTorque = mass * radius * gravity;
		jd.dampingRatio = 0f;
		jd.frequencyHz = 5;
		world.createJoint(jd);
		
	}
	
	private Mesh buildMesh(Texture texture, Vector2 bottomLeft,
			Vector2 size, Body[][] bodiesMat) {
		
		// n x n bodies => (n-1)^2 squares => 2 * (n-1)^2 triangles 
		
		Mesh mesh = new Mesh(true, 4 * n * n, 3 * 2 * (n - 1) * (n - 1), 
                new VertexAttribute(Usage.Position, 2, "a_position"),
                new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
		float vertices[] = new float[4 * n * n];
		short indices[] = new short[3 * 2 * (n - 1) * (n - 1)];
		
		float dxTex = size.x / (n - 1);
		float dyTex = size.y / (n - 1);
		for (int i = 0; i < n; i ++) {
			for (int j = 0; j < n; j++) {
				int v = n * i + j;
				Vector2 pos = bodies[i][j].getPosition();
				vertices[4 * v + 0] = pos.x; 
				vertices[4 * v + 1] = pos.y;
				vertices[4 * v + 2] = bottomLeft.x + i * dxTex; 
				vertices[4 * v + 3] = bottomLeft.y - j * dyTex; // up side down !!!
				//if (i == 0 && j == n - 1)
					//Gdx.app.error("y = ", "" + 256 * (bottomLeft.y - j * dyTex));
			}
		}
		
		// indices
		for (int i = 0; i < n - 1; i ++) {
			for (int j = 0; j < n - 1; j++) {
				int tri = 2 * ((n - 1) * i + j); 
				indices[3 * tri + 0] = vertex(i, j); 
				indices[3 * tri + 1] = vertex(i + 1, j);
				indices[3 * tri + 2] = vertex(i + 1, j + 1);
				tri += 1;
				indices[3 * tri + 0] = vertex(i, j); 
				indices[3 * tri + 1] = vertex(i + 1, j + 1);
				indices[3 * tri + 2] = vertex(i, j + 1);
			}
		}
		
		mesh.setVertices(vertices);			// upper left
		mesh.setIndices(indices);

		return mesh;
		
		
	}

	short vertex(int i, int j) {
		return (short) (i * n + j);
	}
	private final Vector2 tmp = new Vector2();

	public boolean keyDown (int keyCode) {
		if (keyCode == Keys.W) {
			/*
			Vector2 f = m_body.getWorldVector(tmp.set(0, -200));
			Vector2 p = m_body.getWorldPoint(tmp.set(0, 2));
			m_body.applyForce(f, p);
			*/
			bodies[0][n-1].applyForce(new Vector2(0, n * 20), new Vector2(0, 0));
			//bodies[n-1][n-1].applyForce(new Vector2(0, n * 20), new Vector2(0, 0));
		}
		if (keyCode == Keys.A) {
			//bodies[0][0].applyForce(new Vector2(- n * 200, 0), new Vector2(0, 0));
			//bodies[n-1][0].applyForce(new Vector2(- n * 200, 0), new Vector2(0, 0));
			bodies[1][1].applyLinearImpulse(new Vector2(- n * 20, 0), new Vector2(0, 0));
			//bodies[n-1][0].applyLinearImpulse(new Vector2(- n * 2, 0), new Vector2(0, 0));
			bodies[0][0].setType(BodyType.DynamicBody);
			bodies[n-1][0].setType(BodyType.DynamicBody);
			//bodies[0][n - 1].applyForce(new Vector2(- n * 200, 0), new Vector2(0, 0));
		}
			//m_body.applyTorque(50);
		if (keyCode == Keys.D) {//
			//bodies[n/2][n/2].setLinearVelocity(new Vector2(50, 0));
			bodies[0][0].setType(BodyType.DynamicBody);
			bodies[n-1][0].setType(BodyType.DynamicBody);
			bodies[1][1].applyForce(new Vector2(n * 2000, 0), new Vector2(0, 0));
			//bodies[n-1][0].applyForce(new Vector2(n * 200, 0), new Vector2(0, 0));
			//bodies[0][n - 1].applyForce(new Vector2(n * 200, 0), new Vector2(0, 0));
			//m_body.applyTorque(-50);
		}
		if (keyCode == Keys.X) { 
			bodies[0][0].setType(BodyType.StaticBody);
			bodies[n-1][0].setType(BodyType.StaticBody);
			//bodies[1][1].setType(BodyType.StaticBody);
			//m_body.applyTorque(-50);
		}
		return false;
	}
	
	@Override
	public void render () {
		// update the world with a fixed time step
		long startTime = TimeUtils.nanoTime();
		world.step(Gdx.app.getGraphics().getDeltaTime(), 3, 3);
		float updateTime = (TimeUtils.nanoTime() - startTime) / 1000000000.0f;

		startTime = TimeUtils.nanoTime();
		// clear the screen and setup the projection matrix
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		// render the world using the debug renderer
		
		renderer.render(world, camera.combined);
		float renderTime = (TimeUtils.nanoTime() - startTime) / 1000000000.0f;

			
		if (frames > 600) {
			batch.begin();
			font.draw(batch, "fps:" + Gdx.graphics.getFramesPerSecond() + ", update: " + updateTime + ", render: " + renderTime, 0, 20);
			batch.end();
		} else {
			GL10 gl = Gdx.graphics.getGL10();
			gl.glEnable(GL10.GL_TEXTURE_2D);
			
			Mesh mesh = buildMesh(jelly, new Vector2(57f/256f, 57f/256f),
					new Vector2(48f/256f, 48f/256f), bodies);
			
			
			
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	        
	
	        jelly.bind();
	        
	        gl.glPushMatrix();
	        gl.glTranslatef(0.3f, 0.0f, 0.0f);
	        gl.glScalef(0.5f, 0.5f, 1.0f);
	        gl.glRotatef(0, 0f, 0f, 1f);
	        mesh.render(GL10.GL_TRIANGLES);
	        gl.glPopMatrix();
		}
		
		if (frames++ >= 1200)
			frames = 0;
	}
}
