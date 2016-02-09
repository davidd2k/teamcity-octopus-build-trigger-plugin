/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

class NoChangedDeploymentsException extends Exception {
  public NoChangedDeploymentsException(String message) {
    super(message);
  }

  public NoChangedDeploymentsException(Deployments oldDeployments, Deployments newDeployments) {
    this(String.format("Didn't find any differences between '%s' and '%s'.",
      oldDeployments.toString(), newDeployments.toString()));
  }

  public NoChangedDeploymentsException(String message, Throwable cause) {
    super(message, cause);
  }
}
