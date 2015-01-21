using UnityEngine;
using System.Collections;

public class LocalPlayerS : MonoBehaviour {

	public Vector3 position;
	public Vector3 velocity;
	public string id;
	
	void FixedUpdate()
	{
		transform.Translate(velocity);
	}
	
	
}
