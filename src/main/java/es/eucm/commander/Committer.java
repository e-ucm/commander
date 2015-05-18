/**
 * Copyright (C) 2015 e-UCM Research Group (e-adventure-dev@e-ucm.es)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.eucm.commander;

/**
 * Commits resources to a persistent system
 */
public interface Committer {

	/**
	 * Saves the resource to the persistent system. This method does not need to
	 * change the state of resource to commit
	 */
	void commitModified(Resource resource);

	/**
	 * Removes the resource from the persistent system
	 */
	void commitRemoved(Resource resource);
}
